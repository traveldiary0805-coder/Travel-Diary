package com.traveldiary.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traveldiary.app.data.repository.AuthRepositoryImpl
import com.traveldiary.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onEmailChange(email: String) =
        _uiState.update { it.copy(email = email) }

    fun onPasswordChange(password: String) =
        _uiState.update { it.copy(password = password) }

    fun toggleMode() =
        _uiState.update {
            it.copy(
                mode = if (it.mode == AuthMode.LOGIN)
                    AuthMode.REGISTER else AuthMode.LOGIN,
                errorMessage = null
            )
        }

    fun submit(onSuccess: () -> Unit) {

        val state = _uiState.value

        if (!state.canSubmit) {
            _uiState.update {
                it.copy(errorMessage = "Please enter valid email and password")
            }
            return
        }

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }

            try {

                if (state.mode == AuthMode.LOGIN) {
                    repository.login(state.email, state.password)
                } else {
                    repository.register(state.email, state.password)
                }

                _uiState.update { it.copy(isLoading = false) }

                onSuccess()

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Authentication failed"
                    )
                }
            }
        }
    }

//    fun loginWithGoogle(onSuccess: () -> Unit) {
//        viewModelScope.launch {
//            try {
//                repository.loginWithGoogle()
//                onSuccess()
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(errorMessage = e.message ?: "Google login failed")
//                }
//            }
//        }
//    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onLoggedOut()
        }
    }

    fun clearError() =
        _uiState.update { it.copy(errorMessage = null) }
}