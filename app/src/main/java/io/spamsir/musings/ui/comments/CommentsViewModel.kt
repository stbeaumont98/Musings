package io.spamsir.musings.ui.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.data.database.CommentDatabase
import io.spamsir.musings.data.database.CommentDatabaseDao
import io.spamsir.musings.data.database.NoteDatabase
import io.spamsir.musings.data.database.NoteDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val annotationDao: CommentDatabaseDao,
    private val noteDao: NoteDatabaseDao
): ViewModel() {

    val mutableState = MutableStateFlow(CommentsState())
    val state = mutableState.asStateFlow()

    fun loadData(key: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.value = mutableState.value.copy(
                noteId = key,
                title = noteDao.get(key).title,
                content = noteDao.get(key).content,
                isLiked = noteDao.get(key).isLiked,
                comments = annotationDao.getList(key)
            )
        }
    }

    fun onEvent(event: CommentEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                is CommentEvent.NewComment -> {
                    annotationDao.insert(event.comment)
                }

                is CommentEvent.UpdateComment -> {
                    annotationDao.update(
                        event.comment.dateTime,
                        event.comment.content,
                        event.comment.noteId,
                        event.comment.commentId
                    )
                }

                is CommentEvent.RemoveComment -> {
                    annotationDao.remove(event.id)
                }

                is CommentEvent.UpdateNote -> {
                    noteDao.update(event.key, event.isLiked)
                }
            }
            loadData(state.value.noteId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val annotationDataSource = CommentDatabase.getDatabase(application.applicationContext, CoroutineScope(Dispatchers.IO)).commentDatabaseDao
                val noteDataSource = NoteDatabase.getDatabase(application.applicationContext, CoroutineScope(
                    Dispatchers.IO)
                ).noteDatabaseDao

                return CommentsViewModel(annotationDataSource, noteDataSource) as T
            }
        }
    }
}