package com.traveldiary.app.domain.repository

import com.traveldiary.app.domain.model.Entry
import kotlinx.coroutines.flow.Flow
import java.io.File

interface EntryRepository {

    fun getEntries(): Flow<List<Entry>>

    suspend fun getEntryById(id: String): Entry?

    suspend fun addEntry(
        imageFile: File,
        note: String
    )

    suspend fun updateEntry(
        entryId: String,
        imageFile: File?,
        note: String
    )

    suspend fun deleteEntry(entryId: String)
}