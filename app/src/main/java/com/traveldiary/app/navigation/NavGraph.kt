package com.traveldiary.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.traveldiary.app.presentation.auth.AuthScreen
import com.traveldiary.app.presentation.home.HomeScreen
import com.traveldiary.app.presentation.splash.SplashScreen
import com.traveldiary.app.presentation.splash.SplashViewModel

@Composable
fun TravelDiaryNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Splash
    ) {

        composable<Splash> {

            val viewModel: SplashViewModel = viewModel()

            SplashScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Home) {
                        popUpTo<Splash> { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Auth) {
                        popUpTo<Splash> { inclusive = true }
                    }
                }
            )
        }

        composable<Auth> {

            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Home) {
                        popUpTo<Auth> { inclusive = true }
                    }
                }
            )
        }

        composable<Home> {
            HomeScreen()
        }
    }
}