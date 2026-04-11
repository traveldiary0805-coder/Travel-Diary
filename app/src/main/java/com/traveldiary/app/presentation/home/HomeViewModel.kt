package com.traveldiary.app.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traveldiary.app.data.repository.EntryRepositoryImpl
import com.traveldiary.app.utils.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val context: Context
) : ViewModel() {

    private val repository = EntryRepositoryImpl(context)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val networkMonitor = NetworkMonitor(context)

    init {
        observeNetwork()
        loadEntries()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isConnected.collect { connected ->
                if (connected) {
                    repository.syncWithRemote()
                    loadEntries()
                }
            }
        }
    }

    fun loadEntries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.syncWithRemote()

            try {
                repository.getEntries().collectLatest { entries ->
                    _uiState.update {
                        it.copy(entries = entries, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.syncWithRemote()
            loadEntries()
        }
    }
}