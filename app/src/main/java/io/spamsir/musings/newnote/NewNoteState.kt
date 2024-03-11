package io.spamsir.musings.newnote

import io.spamsir.musings.data.Note
import io.spamsir.musings.data.Settings
import java.util.Calendar

data class NewNoteState(val nextTime: Long = Calendar.getInstance().timeInMillis, val noteToday: Note? = null, val settings: Settings = Settings())
