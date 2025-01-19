package com.example.noteapp.model

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.noteapp.Constants

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = Constants.TABLE_NAME, indices = [Index(value = ["id"], unique = true)])
data class Note (
    @PrimaryKey(autoGenerate = true)    val id: Int? = null,
    @ColumnInfo(name = "note")          val note: String,
    @ColumnInfo(name = "title")         val title: String,
    @ColumnInfo(name = "dateUpdated")   val dateUpdated: String = getDateCreated(),
    @ColumnInfo(name = "imageUri")     val imageUri: String? = null
)

@SuppressLint("NewApi")
fun getDateCreated(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
}

@SuppressLint("NewApi")
fun Note.getDay(): String{
    if (this.dateUpdated.isEmpty()) return ""
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
    return LocalDateTime.parse(this.dateUpdated,formatter ).toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
}

val placeHolderList = listOf(Note(id = 0, title = "Cannot find any notes", note = "Cannot find note details"))

fun List<Note>?.orPlaceHolderList(): List<Note> {
    return if (this != null && this.isNotEmpty()){
        this
    }else placeHolderList
}