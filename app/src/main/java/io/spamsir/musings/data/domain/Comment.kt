package io.spamsir.musings.data.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "comments_table")
data class Comment(
    @ColumnInfo(name = "date_time")
    val dateTime: Long = Calendar.getInstance().timeInMillis,
    @ColumnInfo(name = "content")
    val content: String = "",
    @ColumnInfo(name = "note_id")
    val noteId: Long = 0L
) {
    @PrimaryKey(autoGenerate = true)
    var commentId: Long = 0L
}