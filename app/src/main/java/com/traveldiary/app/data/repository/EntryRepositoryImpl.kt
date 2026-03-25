package com.traveldiary.app.data.repository

import android.util.Log
import com.traveldiary.app.data.dto.EntryDto
import com.traveldiary.app.data.mapper.toDomain
import com.traveldiary.app.data.remote.SupabaseManager
import com.traveldiary.app.domain.model.Entry
import com.traveldiary.app.domain.repository.EntryRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.UUID

class EntryRepositoryImpl : EntryRepository {

    private val client = SupabaseManager.client

    override fun getEntries(): Flow<List<Entry>> = flow {

        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("User not authenticated")

        val response = client.postgrest["entries"]
            .select {
                order(
                    column = "created_at",
                    order = Order.DESCENDING
                )
            }
            .decodeList<EntryDto>()

        emit(
            response
                .map { it.toDomain() }
                .sortedByDescending { it.createdAt }
        )
    }

    override suspend fun getEntryById(id: String): Entry? {

        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("User not authenticated")

        val response = client.postgrest["entries"]
            .select {
                filter {
                    eq("id", id)
                    eq("user_id", userId)
                }
            }
            .decodeSingleOrNull<EntryDto>()

        return response?.toDomain()
    }

    override suspend fun addEntry(
        imageFile: File,
        note: String
    ) {

        val user = client.auth.currentUserOrNull()
            ?: throw Exception("User not authenticated")

        val fileName = "${UUID.randomUUID()}.jpg"

        val bytes = imageFile.readBytes()

        client.storage["travel-images"].upload(
            path = fileName,
            data = bytes
        ) {
            upsert = false
        }

        val imageUrl = client.storage["travel-images"]
            .publicUrl(fileName)

        Log.d("RLS_DEBUG", "Auth user id = ${user.id}")

        client.postgrest["entries"].insert(
            mapOf(
                "user_id" to user.id,
                "image_url" to imageUrl,
                "note" to note
            )
        )
    }
}