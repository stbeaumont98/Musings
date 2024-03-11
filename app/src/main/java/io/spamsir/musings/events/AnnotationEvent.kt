package io.spamsir.musings.events

import io.spamsir.musings.data.Annotation

sealed class AnnotationEvent {
    data class NewAnnotation(val annotation: Annotation) : AnnotationEvent()
    data class UpdateAnnotation(val annotation: Annotation) : AnnotationEvent()
    data class RemoveAnnotation(val id: Long) : AnnotationEvent()
    data class UpdateNote(val key: Long, val isLiked: Boolean) : AnnotationEvent()
}