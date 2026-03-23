package com.traveldiary.app.presentation.addedit

data class AddEditEntryUiState(
    val entryId: String? = null,
    val imageUrl: String? = null,
    val note: String = "",
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false
)