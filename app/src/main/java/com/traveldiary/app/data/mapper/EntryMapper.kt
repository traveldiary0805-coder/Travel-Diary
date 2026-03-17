package com.traveldiary.app.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.traveldiary.app.data.dto.EntryDto
import com.traveldiary.app.domain.model.Entry
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
fun EntryDto.toDomain(): Entry {
    return Entry(
        id = id,
        imageUrl = imageUrl,
        note = note,
        createdAt = Instant.parse(createdAt)
    )
}