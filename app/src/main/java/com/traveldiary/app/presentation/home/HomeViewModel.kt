package com.traveldiary.app.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traveldiary.app.data.local.AppDatabase
import com.traveldiary.app.data.local.CachedEntryEntity
import com.traveldiary.app.data.repository.EntryRepositoryImpl
import com.traveldiary.app.domain.model.Entry
import com.traveldiary.app.utils.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    context: Context
) : ViewModel() {

    private val database = AppDatabase.getDatabase(context)
    private val dao = database.cachedEntryDao()
    private val repository = EntryRepositoryImpl()
    private val networkMonitor = NetworkMonitor(context)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        observeNetwork()
        loadEntries()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isConnected.collectLatest { connected ->
                if (connected) {
                    loadEntries()
                }
            }
        }
    }

    fun loadEntries() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {

                val localEntries = dao.getAll()

                if (localEntries.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            entries = localEntries.map { entity ->
                                Entry(
                                    id = entity.id,
                                    imageUrl = entity.imageUrl,
                                    note = entity.note,
                                    createdAt = entity.createdAt
                                )
                            },
                            isLoading = false
                        )
                    }
                }

                try {
                    val remoteEntries = repository.getEntriesOnce()

                    dao.clearAll()
                    dao.insertAll(
                        remoteEntries.map { entry ->
                            CachedEntryEntity(
                                id = entry.id,
                                imageUrl = entry.imageUrl,
                                note = entry.note,
                                createdAt = entry.createdAt
                            )
                        }
                    )

                    _uiState.update {
                        it.copy(
                            entries = remoteEntries,
                            isLoading = false
                        )
                    }

                } catch (e: Exception) {
                    if (localEntries.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "No internet connection"
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Something went wrong"
                    )
                }
            }
        }
    }
}