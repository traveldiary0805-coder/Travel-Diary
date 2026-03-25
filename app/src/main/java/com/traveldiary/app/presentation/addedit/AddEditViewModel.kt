package com.traveldiary.app.presentation.addedit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traveldiary.app.data.repository.EntryRepositoryImpl
import kotlinx.coroutines.launch
import java.io.File

class AddEditViewModel : ViewModel() {

    private val repository = EntryRepositoryImpl()

    fun saveEntry(
        imageFile: File?,
        note: String,
        onSuccess: () -> Unit
    ) {
        if (imageFile == null || note.isBlank()) {
            return
        }

        viewModelScope.launch {
            try {
                repository.addEntry(imageFile, note)
                onSuccess()
            } catch (e: Exception) {
                Log.e("SAVE_DEBUG", "Upload failed", e)
            }
        }
    }
}