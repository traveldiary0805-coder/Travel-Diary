package com.traveldiary.app.domain.model

import java.time.Instant

data class Entry(
    val id: String,
    val imageUrl: String,
    val note: String,
    val createdAt: Instant
)