package io.spamsir.musings

import java.util.Calendar

data class NoteState(val dateTime: Long = Calendar.getInstance().timeInMillis, val title: String = "", val content: String = "", val isLiked: Boolean = false)
