@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.noteapp.ui.noteEdit

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.noteapp.Constants
import com.example.noteapp.NotesApp
import com.example.noteapp.NotesViewModel
import com.example.noteapp.R
import com.example.noteapp.model.Note
import com.example.noteapp.ui.GenericAppBar
import com.example.noteapp.ui.noteList.NotesFab
import com.example.noteapp.ui.theme.NoteAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteEditScreen(noteId: Int, navController: NavController, viewModel: NotesViewModel) {
    val scope = rememberCoroutineScope()
    val note = remember {
        mutableStateOf(Constants.noteDetailPlaceHolder)
    }

    val currentNote = remember { mutableStateOf(note.value.note) }
    val currentTitle = remember { mutableStateOf(note.value.title) }
    val currentPhotos = remember { mutableStateOf(note.value.imageUri) }
    val saveButtonState = remember { mutableStateOf(false) }

    val getImageRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {
            NotesApp.getUriPermission(uri)
        }
        currentPhotos.value = uri.toString()
        if (currentPhotos.value != note.value.imageUri) {
            saveButtonState.value = true
        }
    }

    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            note.value = viewModel.getNote(noteId) ?: Constants.noteDetailPlaceHolder
            currentNote.value = note.value.note
            currentTitle.value = note.value.title
            currentPhotos.value = note.value.imageUri
        }
    }


    NoteAppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    GenericAppBar(
                        title = "Edit Note",
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.save),
                                contentDescription = stringResource(R.string.save_note),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        onIconClick = {
                            viewModel.updateNote(
                                Note(
                                    id = note.value.id,
                                    note = currentNote.value,
                                    title = currentTitle.value,
                                    imageUri = currentPhotos.value
                                )
                            )
                            navController.popBackStack()
                        },
                        iconState = saveButtonState
                    )
                },
                floatingActionButton = {
                    NotesFab(
                        contentDescription = stringResource(R.string.add_photo),
                        action = {
                            getImageRequest.launch(arrayOf("image/*"))
                        },
                        icon = R.drawable.camera
                    )
                },
                content = { paddingValues ->
                    EditNoteContent(
                        modifier = Modifier.padding(paddingValues),
                        currentTitle = currentTitle,
                        currentNote = currentNote,
                        currentPhotos = currentPhotos,
                        saveButtonState = saveButtonState,
                        note = note.value
                    )
                }
            )
        }
    }
}
@Composable
fun EditNoteContent(
    modifier: Modifier = Modifier,
    currentTitle: MutableState<String>,
    currentNote: MutableState<String>,
    currentPhotos: MutableState<String?>,
    saveButtonState: MutableState<Boolean>,
    note: Note
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {

        currentPhotos.value?.let {
            if (it.isNotEmpty()) {
                ImagePreview(photoUri = it)
            }
        }

        NoteTextField(
            value = currentTitle.value,
            onValueChange = { value ->
                currentTitle.value = value
                updateSaveButtonState(currentTitle, currentNote, saveButtonState, note)
            },
            label = "Title",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(12.dp))


        NoteTextField(
            value = currentNote.value,
            onValueChange = { value ->
                currentNote.value = value
                updateSaveButtonState(currentTitle, currentNote, saveButtonState, note)
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
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
            .padding(6.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun NoteTextField(
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

fun updateSaveButtonState(
    currentTitle: MutableState<String>,
    currentNote: MutableState<String>,
    saveButtonState: MutableState<Boolean>,
    note: Note
) {
    // Logic for determining when save button should be enabled
    saveButtonState.value = currentTitle.value != note.title || currentNote.value != note.note
}