package com.traveldiary.app.presentation.addedit

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

            shape = RoundedCornerShape(28.dp),

            containerColor = MaterialTheme.colorScheme.surface,

            title = {
                Text(
                    text = "Delete Memory",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },

            text = {
                Text(
                    text = "Are you sure you want to permanently delete this memory? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },

            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        entryId?.let {
                            viewModel.deleteEntry(it) {
                                onNavigateBack()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        "Delete",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },

            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        "Cancel",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
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