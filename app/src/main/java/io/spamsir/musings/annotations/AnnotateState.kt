package io.spamsir.musings.annotations

import io.spamsir.musings.data.Annotation

data class AnnotateState(val noteId: Long = 0L, val title: String = "", val content: String = "", val isLiked: Boolean = false, val annotations: List<Annotation> = listOf())
