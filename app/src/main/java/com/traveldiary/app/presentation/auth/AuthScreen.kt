package com.traveldiary.app.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.traveldiary.app.ui.theme.TravelDiaryTheme

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    AuthContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = { viewModel.submit(onAuthSuccess) },
        onToggleMode = viewModel::toggleMode,
        onErrorShown = viewModel::clearError
    )
}


@Preview(showBackground = true)
@Composable
fun AuthLoginPreview() {
    TravelDiaryTheme {
        AuthContent(
            state = AuthUiState(
                email = "test@test.com",
                password = "123456",
                mode = AuthMode.LOGIN
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onSubmit = {},
            onToggleMode = {},
            onErrorShown = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthRegisterPreview() {
    TravelDiaryTheme {
        AuthContent(
            state = AuthUiState(
                email = "",
                password = "",
                mode = AuthMode.REGISTER
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onSubmit = {},
            onToggleMode = {},
            onErrorShown = {}
        )
    }
}