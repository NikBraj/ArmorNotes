package com.example.noteapp.ui.noteDetail

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.noteapp.Constants
import com.example.noteapp.Constants.noteDetailPlaceHolder
import com.example.noteapp.NotesViewModel
import com.example.noteapp.R
import com.example.noteapp.ui.GenericAppBar
import com.example.noteapp.ui.theme.NoteAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.noteapp.model.Note


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteDetailScreen(noteId: Int, navController: NavController, viewModel: NotesViewModel) {
    val scope = rememberCoroutineScope()
    val note = remember {
        mutableStateOf(noteDetailPlaceHolder)
    }


    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            note.value = viewModel.getNote(noteId) ?: noteDetailPlaceHolder
        }
    }

    NoteAppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    GenericAppBar(
                        title = note.value.title,
                        onIconClick = {
                            navController.navigate(Constants.noteEditNavigation(note.value.id ?: 0))
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.edit_note),
                                contentDescription = stringResource(R.string.edit_note),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        iconState = remember { mutableStateOf(true) }
                    )
                },
                content = { paddingValues ->
                    NoteDetailContent(
                        modifier = Modifier.padding(paddingValues),
                        note = note.value
                    )
                }
            )
        }
    }
}

@Composable
fun NoteDetailContent(
    modifier: Modifier = Modifier,
    note: Note
) {
    Column(modifier) {
        note.imageUri?.let {
            if (it.isNotEmpty()) {
                ImagePreview(photoUri = it)
            }
        }

        NoteTitle(note.title)
        NoteDate(date = note.dateUpdated)
        NoteBody(note.note)
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
            .fillMaxWidth()
            .padding(6.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun NoteTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(top = 24.dp, start = 12.dp, end = 24.dp),
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun NoteDate(date: String) {
    Text(
        text = date,
        modifier = Modifier.padding(12.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    )
}

@Composable
fun NoteBody(body: String) {
    Text(
        text = body,
        modifier = Modifier.padding(12.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
}