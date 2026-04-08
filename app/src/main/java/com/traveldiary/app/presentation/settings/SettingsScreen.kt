package com.traveldiary.app.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.traveldiary.app.ui.theme.TravelDiaryTheme

@Composable
fun SettingsScreen(
    onLogoutNavigate: () -> Unit
) {

    val context = LocalContext.current

    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )

    val googleSignInClient = remember {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, options)
    }

    SettingsContent(
        onLogoutClick = {
            viewModel.logout(googleSignInClient) {
                onLogoutNavigate()
            }
        },
        onClearCacheClick = {
            viewModel.clearCache { }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    TravelDiaryTheme {
        SettingsContent(
            onLogoutClick = {},
            onClearCacheClick = {}
        )
    }
}