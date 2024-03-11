package io.spamsir.musings.ui.newnote

import io.spamsir.musings.data.domain.Note
import io.spamsir.musings.data.domain.Settings

sealed class NewNoteEvent {
    data class SaveSettings(val settings: Settings) : NewNoteEvent()
    data class SaveNote(val note: Note) : NewNoteEvent()
}