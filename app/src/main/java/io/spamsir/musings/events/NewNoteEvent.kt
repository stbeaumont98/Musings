package io.spamsir.musings.events

import io.spamsir.musings.data.Note
import io.spamsir.musings.data.Settings

sealed class NewNoteEvent {
    data class SaveSettings(val settings: Settings) : NewNoteEvent()
    data class SaveNote(val note: Note) : NewNoteEvent()
}