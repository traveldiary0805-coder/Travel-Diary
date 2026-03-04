package com.traveldiary.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traveldiary.app.data.remote.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination

    init {
        observeSession()
    }

    private fun observeSession() {

        viewModelScope.launch {

            SupabaseManager.client.auth.sessionStatus.collect { status ->

                when (status) {

                    is SessionStatus.Authenticated -> {
                        _destination.value = SplashDestination.Home
                    }

                    is SessionStatus.NotAuthenticated -> {
                        _destination.value = SplashDestination.Auth
                    }

                    else -> Unit
                }
            }
        }
    }
}

enum class SplashDestination {
    Home,
    Auth
}