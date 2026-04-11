package com.traveldiary.app.utils

import android.content.Context
import com.traveldiary.app.data.local.AppDatabase
import com.traveldiary.app.data.local.CachedEntryEntity
import com.traveldiary.app.data.remote.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import java.io.File

class SyncManager(
    private val context: Context,
    private val database: AppDatabase
) {

    private var isSyncing = false

    suspend fun syncPending() {
        if (isSyncing) return
        isSyncing = true

        try {
            val pending = database.cachedEntryDao().getPendingSync()

            for (local in pending) {
                try {
                    val user = SupabaseManager.client.auth.currentUserOrNull()
                        ?: continue

                    database.cachedEntryDao().updateSyncStatus(local.id, "Syncing")

                    var imageUrl: String? = null

                    if (local.imageUrl != null && !local.imageUrl.startsWith("http")) {
                        val file = File(local.imageUrl)
                        if (file.exists()) {
                            val bytes = file.readBytes()
                            val fileName = "${local.id}.jpg"

                            SupabaseManager.client.storage["travel-images"]
                                .upload(fileName, bytes) { upsert = true }

                            imageUrl = SupabaseManager.client.storage["travel-images"]
                                .publicUrl(fileName)
                        }
                    }

                    SupabaseManager.client.postgrest["entries"].insert(
                        mapOf(
                            "id" to local.id,
                            "user_id" to user.id,
                            "image_url" to imageUrl,
                            "note" to local.note,
                            "created_at" to local.createdAt
                        )
                    )

                    database.cachedEntryDao().deleteById(local.id)

                } catch (e: Exception) {
                    database.cachedEntryDao().updateSyncStatus(local.id, "PendingSync")
                }
            }

        } finally {
            isSyncing = false
        }
    }
}