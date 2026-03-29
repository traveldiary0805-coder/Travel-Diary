package com.traveldiary.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedEntryDao {

    @Query("SELECT * FROM cached_entries ORDER BY createdAt DESC")
    suspend fun getAll(): List<CachedEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<CachedEntryEntity>)

    @Query("DELETE FROM cached_entries")
    suspend fun clearAll()
}