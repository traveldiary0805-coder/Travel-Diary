package com.traveldiary.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntryDto(
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("image_url")
    val imageUrl: String,

    val note: String,

    @SerialName("created_at")
    val createdAt: String
)