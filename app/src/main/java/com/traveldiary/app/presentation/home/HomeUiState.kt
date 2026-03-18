package com.traveldiary.app.presentation.home

import com.traveldiary.app.domain.model.Entry

data class HomeUiState(
    val isLoading: Boolean = false,
    val entries: List<Entry> = emptyList(),
    val errorMessage: String? = null
)