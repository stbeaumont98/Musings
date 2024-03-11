package io.spamsir.musings.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "annotations_table")
data class Annotation(
    @ColumnInfo(name = "date_time")
    val dateTime: Long = Calendar.getInstance().timeInMillis,
    @ColumnInfo(name = "content")
    val content: String = "",
    @ColumnInfo(name = "note_id")
    val noteId: Long = 0L
) {
    @PrimaryKey(autoGenerate = true)
    var annotationId: Long = 0L
}