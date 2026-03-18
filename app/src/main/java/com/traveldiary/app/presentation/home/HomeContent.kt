package com.traveldiary.app.presentation.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeContent(
    state: HomeUiState
) {
    when {
        state.isLoading -> Text("Loading entries...")
        state.errorMessage != null -> Text("Error: ${state.errorMessage}")
        else -> Text("Entries count: ${state.entries.size}")
    }
}