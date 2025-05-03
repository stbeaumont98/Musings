package io.spamsir.musings.ui.comments

import io.spamsir.musings.data.domain.Comment

sealed class CommentEvent {
    data class NewComment(val comment: Comment) : CommentEvent()
    data class UpdateComment(val comment: Comment) : CommentEvent()
    data class RemoveComment(val id: Long) : CommentEvent()
    data class UpdateNote(val key: Long, val isLiked: Boolean) : CommentEvent()
}