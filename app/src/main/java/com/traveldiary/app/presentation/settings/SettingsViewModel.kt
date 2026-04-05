package com.traveldiary.app.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traveldiary.app.data.local.AppDatabase
import com.traveldiary.app.data.remote.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class SettingsViewModel(
    context: Context
) : ViewModel() {

    private val database = AppDatabase.getDatabase(context)

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            SupabaseManager.client.auth.signOut()
            onLoggedOut()
        }
    }

    fun clearCache(onCleared: () -> Unit) {
        viewModelScope.launch {
            database.cachedEntryDao().clearAll()
            onCleared()
        }
    }
}