package com.traveldiary.app.data.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.traveldiary.app.data.dto.EntryDto
import com.traveldiary.app.data.local.AppDatabase
import com.traveldiary.app.data.local.CachedEntryEntity
import com.traveldiary.app.data.mapper.toDomain
import com.traveldiary.app.data.remote.SupabaseManager
import com.traveldiary.app.domain.model.Entry
import com.traveldiary.app.domain.repository.EntryRepository
import com.traveldiary.app.utils.NetworkMonitor
import com.traveldiary.app.utils.SyncManager
import com.traveldiary.app.utils.isNetworkAvailable
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.UUID

class EntryRepositoryImpl(
    private val context: Context
) : EntryRepository {

    private val client = SupabaseManager.client
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.cachedEntryDao()
    private val syncManager = SyncManager(context, database)

    override fun getEntries(): Flow<List<Entry>> = flow {
        while (true) {
            emit(dao.getAll().map { it.toDomain() })
            delay(1000)
        }
    }

    override suspend fun getEntryById(id: String): Entry? {
        return dao.getAll().find { it.id == id }?.toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun addEntry(imageFile: File, note: String) {
        val user = client.auth.currentUserOrNull()
            ?: throw Exception("User not authenticated")

        val entryId = UUID.randomUUID().toString()
        val createdAt = java.time.Instant.now().toString()

        val destFile = File(context.cacheDir, "entry_${entryId}.jpg")
        imageFile.copyTo(destFile, overwrite = true)

        val entity = CachedEntryEntity(
            id = entryId,
            imageUrl = destFile.absolutePath,
            note = note,
            createdAt = createdAt,
            syncStatus = "PendingSync"
        )

        dao.insert(entity)

        if (isNetworkAvailable(context)) {
            syncManager.syncPending()
        }
    }

    override suspend fun updateEntry(entryId: String, imageFile: File?, note: String) {
        val existing = dao.getAll().find { it.id == entryId } ?: return

        val newImagePath = imageFile?.let {
            val dest = File(context.cacheDir, "entry_${entryId}.jpg")
            it.copyTo(dest, overwrite = true)
            dest.absolutePath
        } ?: existing.imageUrl

        val updated = existing.copy(
            imageUrl = newImagePath,
            note = note,
            syncStatus = "PendingSync"
        )

        dao.insert(updated)

        if (isNetworkAvailable(context)) {
            syncManager.syncPending()
        }
    }

    override suspend fun deleteEntry(entryId: String) {
        dao.deleteById(entryId)

        if (isNetworkAvailable(context)) {
            try {
                val user = client.auth.currentUserOrNull()
                user?.let {
                    client.postgrest["entries"].delete {
                        filter {
                            eq("id", entryId)
                            eq("user_id", user.id)
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    override suspend fun getEntriesOnce(): List<Entry> {
        return dao.getAll().map { it.toDomain() }
    }

    suspend fun syncWithRemote() {
        if (!isNetworkAvailable(context)) return

        syncManager.syncPending()

        try {
            val user = client.auth.currentUserOrNull() ?: return

            val remote = client.postgrest["entries"]
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<EntryDto>()

            dao.deleteSynced()

            remote.forEach {
                dao.insert(
                    CachedEntryEntity(
                        id = it.id,
                        imageUrl = it.imageUrl,
                        note = it.note,
                        createdAt = it.createdAt,
                        syncStatus = "Synced"
                    )
                )
            }

        } catch (e: Exception) {
        }
    }

    private fun CachedEntryEntity.toDomain(): Entry {
        return Entry(
            id = id,
            imageUrl = imageUrl,
            note = note,
            createdAt = createdAt
        )
    }
}