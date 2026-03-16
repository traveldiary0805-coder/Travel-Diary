package com.traveldiary.app.domain.usecase

import com.traveldiary.app.domain.model.Entry
import com.traveldiary.app.domain.repository.EntryRepository

class GetEntryByIdUseCase(
    private val repository: EntryRepository
) {

    suspend operator fun invoke(id: String): Entry? {
        return repository.getEntryById(id)
    }
}