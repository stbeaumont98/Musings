package io.spamsir.musings.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.spamsir.musings.data.domain.Annotation
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Annotation::class], version = 1, exportSchema = false)
abstract class AnnotationDatabase : RoomDatabase() {
    abstract val annotationDatabaseDao: AnnotationDatabaseDao

    companion object {
        @Volatile
        private var Instance: AnnotationDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AnnotationDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AnnotationDatabase::class.java,
                    "annotations_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}