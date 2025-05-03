package io.spamsir.musings.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.spamsir.musings.data.domain.Comment

@Dao
interface CommentDatabaseDao {
    @Insert
    fun insert(comment: Comment)

    @Query("UPDATE comments_table SET date_time = :dateTime, content = :content, note_id = :noteId WHERE commentId = :id")
    fun update(dateTime: Long, content: String, noteId: Long, id: Long)

    @Query("SELECT * FROM comments_table WHERE commentId = :key")
    fun get(key: Long): Comment?

    @Query("SELECT * FROM comments_table WHERE note_id = :key ORDER BY date_time")
    fun getList(key: Long): List<Comment>

    @Query("DELETE FROM comments_table WHERE commentId = :key")
    fun remove(key: Long)

    @Query("DELETE FROM comments_table WHERE note_id = :key")
    fun removeAll(key: Long)
}