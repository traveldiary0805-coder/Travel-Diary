package com.traveldiary.app.presentation.addedit

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.traveldiary.app.ui.theme.TravelDiaryTheme

@Composable
fun AddEditEntryScreen(
    entryId: String? = null,
    onNavigateBack: () -> Unit
) {

    var state by remember {
        mutableStateOf(
            AddEditEntryUiState(
                entryId = entryId,
                isEditMode = entryId != null
            )
        )
    }

    AddEditEntryContent(
        state = state,
        onCaptureClick = { /* Camera in Commit 11 */ },
        onNoteChange = { state = state.copy(note = it) },
        onSaveClick = { onNavigateBack() },
        onDeleteClick = { onNavigateBack() }
    )
}

@Preview(showBackground = true)
@Composable
fun AddEntryPreview() {
    TravelDiaryTheme {
        AddEditEntryContent(
            state = AddEditEntryUiState(),
            onCaptureClick = {},
            onNoteChange = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditEntryPreview() {
    TravelDiaryTheme {
        AddEditEntryContent(
            state = AddEditEntryUiState(
                entryId = "1",
                imageUrl = "https://picsum.photos/400",
                note = "Trip to Goa beach with friends.",
                isEditMode = true
            ),
            onCaptureClick = {},
            onNoteChange = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}