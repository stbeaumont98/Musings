package io.spamsir.musings.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.spamsir.musings.data.domain.Comment
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Comment::class], version = 1, exportSchema = false)
abstract class CommentDatabase : RoomDatabase() {
    abstract val commentDatabaseDao: CommentDatabaseDao

    companion object {
        @Volatile
        private var Instance: CommentDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CommentDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CommentDatabase::class.java,
                    "annotations_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}