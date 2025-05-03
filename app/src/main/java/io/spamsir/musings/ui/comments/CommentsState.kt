package io.spamsir.musings.ui.comments

import io.spamsir.musings.data.domain.Comment

data class CommentsState(val noteId: Long = 0L, val title: String = "", val content: String = "", val isLiked: Boolean = false, val comments: List<Comment> = listOf())
