package io.spamsir.musings.ui.listitems

import io.spamsir.musings.data.domain.Event

sealed class NoteEvent: Event {
    data class UpdateNote(val key: Long, val isLiked: Boolean) : NoteEvent()
}