package io.spamsir.musings.ui.newnote

import io.spamsir.musings.data.domain.Note
import io.spamsir.musings.data.domain.Settings

data class NewNoteState(val noteToday: Note? = null, val settings: Settings = Settings())
