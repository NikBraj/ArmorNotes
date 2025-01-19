package com.example.noteapp

import com.example.noteapp.model.Note

object Constants {
    const val NAVIGATION_NOTES_LIST = "notesList"
    const val NAVIGATION_NOTES_CREATE = "notesCreated"
    const val NAVIGATION_NOTE_DETAIL = "noteDetail/{noteId}"
    const val NAVIGATION_NOTE_EDIT= "noteEdit/{noteId}"
    const val NAVIGATION_NOTE_ID_Argument = "noteId"
    const val TABLE_NAME = "Notes"
    const val DATABASE_NAME = "NotesDatabase"

    fun noteDetailNavigation(noteId : Int) = "noteDetail/$noteId"
    fun noteEditNavigation(noteId : Int) = "noteEdit/$noteId"

    val noteDetailPlaceHolder = Note(note = "Cannot find note details", id = 0, title = "No notes found")
}