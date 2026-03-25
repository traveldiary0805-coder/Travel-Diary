package com.traveldiary.app.presentation.addedit

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

    var state by remember {
        mutableStateOf(
            AddEditEntryUiState(
                entryId = entryId,
                isEditMode = entryId != null
            )
        )
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            state = state.copy(imageUrl = imageUri.toString())
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
            state = state.copy(note = it)
        },

        onSaveClick = {

            viewModel.saveEntry(
                imageFile = imageFile,
                note = state.note
            ) {
                onNavigateBack()
            }
        },

        onDeleteClick = {
            onNavigateBack()
        }
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