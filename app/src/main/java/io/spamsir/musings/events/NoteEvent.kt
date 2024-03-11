package io.spamsir.musings.events

import io.spamsir.musings.data.Note

sealed class NoteEvent {
    data class UpdateNote(val key: Long, val isLiked: Boolean) : NoteEvent()
}