package io.spamsir.musings.ui.listitems

import java.util.Calendar

data class NoteState(val noteId: Long = 0L, val dateTime: Long = Calendar.getInstance().timeInMillis, val title: String = "", val content: String = "", val isLiked: Boolean = false)
