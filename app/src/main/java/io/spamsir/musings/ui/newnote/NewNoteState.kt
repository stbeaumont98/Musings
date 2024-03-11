package io.spamsir.musings.ui.newnote

import io.spamsir.musings.data.domain.Note
import io.spamsir.musings.data.domain.Settings
import java.util.Calendar

data class NewNoteState(val nextTime: Long = Calendar.getInstance().timeInMillis, val noteToday: Note? = null, val settings: Settings = Settings())
