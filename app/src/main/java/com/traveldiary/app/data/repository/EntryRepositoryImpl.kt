package com.traveldiary.app.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.traveldiary.app.data.dto.EntryDto
import com.traveldiary.app.data.mapper.toDomain
import com.traveldiary.app.data.remote.SupabaseManager
import com.traveldiary.app.domain.model.Entry
import com.traveldiary.app.domain.repository.EntryRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EntryRepositoryImpl : EntryRepository {

    private val client = SupabaseManager.client

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getEntries(): Flow<List<Entry>> = flow {

        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("User not authenticated")

        val response = client.postgrest["entries"]
            .select {
                filter {
                    eq("user_id", userId)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<EntryDto>()

        emit(response.map { it.toDomain() })
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
}