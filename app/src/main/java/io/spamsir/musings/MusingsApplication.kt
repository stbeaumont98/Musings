package io.spamsir.musings

import android.app.Application
import android.content.Context
import io.spamsir.musings.data.database.NoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MusingsApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: MusingsApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = applicationContext()
    }

    val database: NoteDatabase by lazy { NoteDatabase.getDatabase(this, CoroutineScope(Dispatchers.IO)) }

}