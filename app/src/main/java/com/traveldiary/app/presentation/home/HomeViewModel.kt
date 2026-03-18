package com.traveldiary.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traveldiary.app.data.repository.EntryRepositoryImpl
import com.traveldiary.app.domain.usecase.GetEntriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = EntryRepositoryImpl()
    private val getEntriesUseCase = GetEntriesUseCase(repository)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadEntries()
    }

    fun loadEntries() {
        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                getEntriesUseCase().collect { entryList ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            entries = entryList
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load entries"
                    )
                }
            }
        }
    }
}