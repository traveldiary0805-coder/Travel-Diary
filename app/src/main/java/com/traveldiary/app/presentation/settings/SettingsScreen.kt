package com.traveldiary.app.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.traveldiary.app.ui.theme.TravelDiaryTheme

@Composable
fun SettingsScreen(
    onLogoutNavigate: () -> Unit
) {

    val context = LocalContext.current

    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )

    SettingsContent(
        onLogoutClick = {
            viewModel.logout {
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