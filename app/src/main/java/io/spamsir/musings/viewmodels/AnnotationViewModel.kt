package io.spamsir.musings.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.data.Annotation
import io.spamsir.musings.database.AnnotationDatabaseDao
import javax.inject.Inject

@HiltViewModel
class AnnotationViewModel @Inject constructor(
    private val annotationDao: AnnotationDatabaseDao
): ViewModel() {

    fun newAnnotation(annotation: Annotation) = annotationDao.insert(annotation)
    fun updateAnnotation(annotation: Annotation) = annotationDao.update(annotation.dateTime, annotation.content, annotation.noteId, annotation.annotationId)
    fun getAnnotations(key: Long): LiveData<List<Annotation>> = annotationDao.getList(key)
    fun removeAnnotation(key: Long) = annotationDao.remove(key)

    companion object {
        @Volatile
        private var instance: AnnotationViewModel? = null

        fun getInstance(annotationDao: AnnotationDatabaseDao) =
            instance ?: synchronized(this) {
                instance ?: AnnotationViewModel(annotationDao).also { instance = it }
            }
    }
}