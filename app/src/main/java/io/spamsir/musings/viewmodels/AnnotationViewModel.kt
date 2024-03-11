package io.spamsir.musings.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.annotations.AnnotateState
import io.spamsir.musings.data.Annotation
import io.spamsir.musings.database.AnnotationDatabase
import io.spamsir.musings.database.AnnotationDatabaseDao
import io.spamsir.musings.database.NoteDatabase
import io.spamsir.musings.database.NoteDatabaseDao
import io.spamsir.musings.events.AnnotationEvent
import io.spamsir.musings.events.NoteEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnotationViewModel @Inject constructor(
    private val annotationDao: AnnotationDatabaseDao,
    private val noteDao: NoteDatabaseDao
): ViewModel() {

    val mutableState = MutableStateFlow(AnnotateState())
    val state = mutableState.asStateFlow()

    fun loadData(key: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.value = mutableState.value.copy(
                noteId = key,
                title = noteDao.get(key).title,
                content = noteDao.get(key).content,
                isLiked = noteDao.get(key).isLiked,
                annotations = annotationDao.getList(key)
            )
        }
    }

    fun onEvent(event: AnnotationEvent) {
        when(event) {
            is AnnotationEvent.NewAnnotation -> { annotationDao.insert(event.annotation) }
            is AnnotationEvent.UpdateAnnotation -> { annotationDao.update(event.annotation.dateTime, event.annotation.content, event.annotation.noteId, event.annotation.annotationId) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val annotationDataSource = AnnotationDatabase.getInstance(application.applicationContext, CoroutineScope(Dispatchers.IO)).annotationDatabaseDao
                val noteDataSource = NoteDatabase.getInstance(application.applicationContext, CoroutineScope(
                    Dispatchers.IO)
                ).noteDatabaseDao

                return AnnotationViewModel(annotationDataSource, noteDataSource) as T
            }
        }
    }
}