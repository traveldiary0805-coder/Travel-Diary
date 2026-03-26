package com.traveldiary.app.presentation.addedit

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.traveldiary.app.ui.theme.TravelDiaryTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddEditEntryScreen(
    entryId: String? = null,
    onNavigateBack: () -> Unit
) {

    val context = LocalContext.current
    val viewModel: AddEditViewModel = viewModel()

    val state by viewModel.uiState.collectAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            viewModel.onImageCaptured(imageUri.toString())
        }
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            context.cacheDir
        )
    }

    LaunchedEffect(entryId) {
        if (entryId != null) {
            viewModel.loadEntry(entryId)
        }
    }

    AddEditEntryContent(
        state = state,

        onCaptureClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val photoFile = createImageFile()
                imageFile = photoFile

                imageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    photoFile
                )

                takePictureLauncher.launch(imageUri!!)

            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },

        onNoteChange = {
            viewModel.onNoteChange(it)
        },

        onSaveClick = {

            if (entryId == null) {
                viewModel.saveEntry(imageFile, state.note) {
                    onNavigateBack()
                }
            } else {
                viewModel.updateEntry(entryId, imageFile, state.note) {
                    onNavigateBack()
                }
            }
        },

        onDeleteClick = {
            showDeleteDialog = true
        }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        entryId?.let {
                            viewModel.deleteEntry(it) {
                                onNavigateBack()
                            }
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this memory?") }
        )
    }
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