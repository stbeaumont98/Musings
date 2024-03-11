package io.spamsir.musings.ui.listitems

sealed class NoteEvent {
    data class UpdateNote(val key: Long, val isLiked: Boolean) : NoteEvent()
}