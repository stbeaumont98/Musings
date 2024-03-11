package io.spamsir.musings.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.spamsir.musings.data.Annotation

@Dao
interface AnnotationDatabaseDao {
    @Insert
    fun insert(annotation: Annotation)

    @Query("UPDATE annotations_table SET date_time = :dateTime, content = :content, note_id = :noteId WHERE annotationId = :id")
    fun update(dateTime: Long, content: String, noteId: Long, id: Long)

    @Query("SELECT * FROM annotations_table WHERE annotationId = :key")
    fun get(key: Long): Annotation?

    @Query("SELECT * FROM annotations_table WHERE note_id = :key ORDER BY date_time")
    fun getList(key: Long): List<Annotation>

    @Query("DELETE FROM annotations_table WHERE annotationId = :key")
    fun remove(key: Long)

    @Query("DELETE FROM annotations_table WHERE note_id = :key")
    fun removeAll(key: Long)
}