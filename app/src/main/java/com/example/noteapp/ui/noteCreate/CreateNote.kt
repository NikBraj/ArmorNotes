@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.noteapp.ui.noteCreate

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.noteapp.NotesApp
import com.example.noteapp.NotesViewModel
import com.example.noteapp.R
import com.example.noteapp.ui.GenericAppBar
import com.example.noteapp.ui.noteList.NotesFab
import com.example.noteapp.ui.theme.NoteAppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateNoteScreen(
    navController: NavController,
    viewModel: NotesViewModel
) {

    val currentNote = remember { mutableStateOf("") }
    val currentTitle = remember { mutableStateOf("") }
    val currentPhotos = remember { mutableStateOf("") }
    val saveButtonState = remember { mutableStateOf(false) }


    val getImageRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        if (it != null) {
            NotesApp.getUriPermission(it)
        }
        currentPhotos.value = it.toString()
    }


    NoteAppTheme {

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
            Scaffold(
                topBar = {
                    GenericAppBar(
                        title = "Create Note",
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.save),
                                contentDescription = stringResource(R.string.save_note),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        onIconClick = {
                            viewModel.createNote(
                                currentTitle.value,
                                currentNote.value,
                                currentPhotos.value
                            )
                            navController.popBackStack()
                        },
                        iconState = saveButtonState
                    )
                },
                floatingActionButton = {
                    NotesFab(
                        contentDescription = stringResource(R.string.add_image),
                        action = { getImageRequest.launch(arrayOf("image/*")) },
                        icon = R.drawable.camera
                    )
                },
                content = { paddingValues ->
                    NoteFormContent(
                        modifier = Modifier.padding(paddingValues),
                        currentTitle = currentTitle,
                        currentNote = currentNote,
                        currentPhotos = currentPhotos,
                        saveButtonState = saveButtonState
                    )
                }
            )
        }
    }
}

@Composable
fun NoteFormContent(
    modifier: Modifier = Modifier,
    currentTitle: MutableState<String>,
    currentNote: MutableState<String>,
    currentPhotos: MutableState<String>,
    saveButtonState: MutableState<Boolean>
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        if (currentPhotos.value.isNotEmpty()) {
            ImagePreview(photoUri = currentPhotos.value)
        }

        NoteInputField(
            value = currentTitle.value,
            onValueChange = { value ->
                currentTitle.value = value
                saveButtonState.value = currentTitle.value.isNotEmpty() && currentNote.value.isNotEmpty()
            },
            label = "Title",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(12.dp))

        NoteInputField(
            value = currentNote.value,
            onValueChange = { value ->
                currentNote.value = value
                saveButtonState.value = currentTitle.value.isNotEmpty() && currentNote.value.isNotEmpty()
            },
            label = "Body",
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun ImagePreview(photoUri: String) {
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(Uri.parse(photoUri))
                .build()
        ),
        contentDescription = null,
        modifier = Modifier
            .fillMaxHeight(0.3f)
            .padding(6.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun NoteInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface
        ),
        label = { Text(text = label) }
    )
}