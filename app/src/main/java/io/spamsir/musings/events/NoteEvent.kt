package io.spamsir.musings.events

import io.spamsir.musings.data.Note

sealed class NoteEvent {
    data class Save(val note: Note) : NoteEvent()
    data object GetTodaysNote : NoteEvent()
}