package com.traveldiary.app.domain.usecase

import com.traveldiary.app.domain.repository.EntryRepository

class GetEntriesUseCase(
    private val repository: EntryRepository
) {

    operator fun invoke() = repository.getEntries()
}