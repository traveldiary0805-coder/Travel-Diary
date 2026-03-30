package com.traveldiary.app.data.mapper

import com.traveldiary.app.data.dto.EntryDto
import com.traveldiary.app.data.local.CachedEntryEntity
import com.traveldiary.app.domain.model.Entry

fun EntryDto.toDomain(): Entry {
    return Entry(
        id = id,
        imageUrl = imageUrl,
        note = note,
        createdAt = createdAt
    )
}

fun CachedEntryEntity.toDomain(): Entry =
    Entry(
        id = id,
        imageUrl = imageUrl ?: "",
        note = note,
        createdAt = createdAt
    )

fun Entry.toEntity(): CachedEntryEntity =
    CachedEntryEntity(
        id = id,
        imageUrl = imageUrl,
        note = note,
        createdAt = createdAt
    )