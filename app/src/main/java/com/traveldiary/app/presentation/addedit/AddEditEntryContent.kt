package com.traveldiary.app.presentation.addedit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntryContent(
    state: AddEditEntryUiState,
    onCaptureClick: () -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isEditMode)
                            "Edit Memory"
                        else
                            "New Memory"
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Image Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (state.imageUrl != null) {
                    AsyncImage(
                        model = state.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "No Image Selected",
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                onClick = onCaptureClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Capture Photo")
            }

            OutlinedTextField(
                value = state.note,
                onValueChange = onNoteChange,
                label = { Text("Write your memory...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                maxLines = 5
            )

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.note.isNotBlank()
            ) {
                Text("Save")
            }

            if (state.isEditMode) {
                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}