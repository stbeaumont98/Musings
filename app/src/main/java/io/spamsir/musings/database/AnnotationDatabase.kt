package io.spamsir.musings.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.spamsir.musings.data.Annotation
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Annotation::class], version = 1, exportSchema = false)
abstract class AnnotationDatabase : RoomDatabase() {
    abstract val annotationDatabaseDao: AnnotationDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: AnnotationDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AnnotationDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AnnotationDatabase::class.java,
                        "annotations_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}