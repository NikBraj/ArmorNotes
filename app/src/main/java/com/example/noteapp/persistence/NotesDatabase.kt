package com.example.noteapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.noteapp.model.Note


@Database(entities = [Note::class], version = 1)

abstract class NotesDatabase : RoomDatabase() {
    abstract fun NotesDao(): NotesDao

}