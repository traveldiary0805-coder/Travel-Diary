package com.traveldiary.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_entries")
data class CachedEntryEntity(
    @PrimaryKey
    val id: String,
    val imageUrl: String?,
    val note: String,
    val createdAt: String,
    val syncStatus: String = "PendingSync"
)