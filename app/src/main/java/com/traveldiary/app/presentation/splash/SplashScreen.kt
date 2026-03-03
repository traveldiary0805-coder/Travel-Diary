package com.traveldiary.app.presentation.splash

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.traveldiary.app.ui.theme.TravelDiaryTheme

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToAuth: () -> Unit
) {

    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Home -> onNavigateToHome()
            SplashDestination.Auth -> onNavigateToAuth()
            null -> Unit
        }
    }

    SplashContent()
}


@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    TravelDiaryTheme {
        SplashContent()
    }
}