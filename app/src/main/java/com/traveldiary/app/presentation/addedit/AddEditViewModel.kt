package com.traveldiary.app.presentation.addedit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traveldiary.app.data.repository.EntryRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AddEditViewModel : ViewModel() {

    private val repository = EntryRepositoryImpl()

    private val _uiState = MutableStateFlow(AddEditEntryUiState())
    val uiState: StateFlow<AddEditEntryUiState> = _uiState

    fun loadEntry(entryId: String) {
        viewModelScope.launch {

            val entry = repository.getEntryById(entryId)

            entry?.let {
                _uiState.value = AddEditEntryUiState(
                    entryId = entryId,
                    imageUrl = it.imageUrl,
                    note = it.note,
                    isEditMode = true
                )
            }
        }
    }

    fun onNoteChange(note: String) {
        _uiState.update {
            it.copy(note = note)
        }
    }

    fun onImageCaptured(uri: String) {
        _uiState.update {
            it.copy(imageUrl = uri)
        }
    }

    fun saveEntry(
        imageFile: File?,
        note: String,
        onSuccess: () -> Unit
    ) {
        if (imageFile == null || note.isBlank()) return

        viewModelScope.launch {
            repository.addEntry(imageFile, note)
            onSuccess()
        }
    }

    fun updateEntry(
        entryId: String,
        imageFile: File?,
        note: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.updateEntry(entryId, imageFile, note)
            onSuccess()
        }
    }

    fun deleteEntry(
        entryId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.deleteEntry(entryId)
            onSuccess()
        }
    }
}