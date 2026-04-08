package com.traveldiary.app.presentation.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.traveldiary.app.ui.theme.TravelDiaryTheme

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    val googleSignInClient = remember {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1031918550382-5p0muq196ho28eju5f5uk81lg8jm2bre.apps.googleusercontent.com")
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, options)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken != null) {
                    viewModel.loginWithGoogleToken(idToken, onAuthSuccess)
                }

            } catch (e: Exception) {
                Log.e("GOOGLE_LOGIN", "Error: ${e.message}")
            }
        }
    }

    AuthContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = { viewModel.submit(onAuthSuccess) },
        onToggleMode = viewModel::toggleMode,
        onErrorShown = viewModel::clearError,
        onGoogleClick = {
            launcher.launch(googleSignInClient.signInIntent)
        }
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
            onErrorShown = {},
            onGoogleClick = {}
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
            onErrorShown = {},
            onGoogleClick = {}
        )
    }
}