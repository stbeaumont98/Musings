package io.spamsir.musings.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.spamsir.musings.data.Note

@Dao
interface NoteDatabaseDao {
    @Insert
    fun insert(note: Note)

    @Query("UPDATE notes_table SET is_liked = :isLiked WHERE noteId = :key")
    fun update(key: Long, isLiked: Boolean)

    @Query("SELECT * FROM notes_table WHERE noteId = :key")
    fun get(key: Long): Note

    @Query("SELECT EXISTS (SELECT * from notes_table WHERE date_time > :date AND date_time < :date + 86400000)")
    fun noteExistsOnDate(date: Long): Boolean

    @Query("SELECT * FROM notes_table WHERE date_time > :date AND date_time < :date + 86400000 ORDER BY noteId DESC LIMIT 1")
    fun getNoteFromDate(date: Long): Note

    @Query("DELETE FROM notes_table")
    fun clear()

    @Query("SELECT * FROM notes_table WHERE date_time < :date ORDER BY noteId DESC LIMIT 4")
    fun getRecentNotes(date: Long): List<Note>

    @Query("SELECT * FROM notes_table WHERE is_liked = 1 ORDER BY noteId DESC")
    fun getLikedNotes(): List<Note>

    @Query("SELECT * FROM notes_table WHERE date_time < :date AND is_liked = 1 ORDER BY noteId DESC LIMIT 4")
    fun getRecentLikedNotes(date: Long): List<Note>

    @Query("SELECT * FROM notes_table ORDER BY noteId DESC")
    fun getAllNotes(): List<Note>
}