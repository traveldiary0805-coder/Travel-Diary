package com.traveldiary.app.data.local

import androidx.room.*

@Dao
interface CachedEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CachedEntryEntity)

    @Query("SELECT * FROM cached_entries ORDER BY createdAt DESC")
    suspend fun getAll(): List<CachedEntryEntity>

    @Query("SELECT * FROM cached_entries WHERE syncStatus = 'PendingSync'")
    suspend fun getPendingSync(): List<CachedEntryEntity>

    @Query("DELETE FROM cached_entries")
    suspend fun clearAll()

    @Query("DELETE FROM cached_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM cached_entries WHERE syncStatus = 'Synced'")
    suspend fun deleteSynced()

    @Query("UPDATE cached_entries SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String)
}