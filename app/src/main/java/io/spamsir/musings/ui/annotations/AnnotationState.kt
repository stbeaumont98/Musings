package io.spamsir.musings.ui.annotations

import io.spamsir.musings.data.domain.Annotation

data class AnnotationState(val noteId: Long = 0L, val title: String = "", val content: String = "", val isLiked: Boolean = false, val annotations: List<Annotation> = listOf())
