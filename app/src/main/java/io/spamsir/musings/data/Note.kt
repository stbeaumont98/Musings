package io.spamsir.musings.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "notes_table")
data class Note(
    @ColumnInfo(name = "date_time")
    val dateTime: Long = Calendar.getInstance().timeInMillis,
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "content")
    val content: String = "",
    @ColumnInfo(name = "is_liked")
    val isLiked: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var noteId: Long = 0L
}