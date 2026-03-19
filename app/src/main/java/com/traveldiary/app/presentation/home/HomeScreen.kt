package com.traveldiary.app.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.traveldiary.app.domain.model.Entry

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onEntryClick: (String) -> Unit,
    onAddClick: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    HomeContent(
        state = state,
        onEntryClick = onEntryClick,
        onAddClick = onAddClick
    )
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {

    val fakeEntries = listOf(
        Entry(
            id = "1",
            imageUrl = "https://picsum.photos/400",
            note = "Beautiful sunset at Goa beach with friends.",
            createdAt = "2026-03-02T10:28:52"
        ),
        Entry(
            id = "2",
            imageUrl = "https://picsum.photos/401",
            note = "Explored the mountains and enjoyed nature.",
            createdAt = "2026-03-02T10:28:52"
        )
    )

    HomeContent(
        state = HomeUiState(
            entries = fakeEntries
        ),
        onEntryClick = {},
        onAddClick = {}
    )
}