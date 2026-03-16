package com.traveldiary.app.domain.repository

import com.traveldiary.app.domain.model.Entry
import kotlinx.coroutines.flow.Flow

interface EntryRepository {

    fun getEntries(): Flow<List<Entry>>

    suspend fun getEntryById(id: String): Entry?
}