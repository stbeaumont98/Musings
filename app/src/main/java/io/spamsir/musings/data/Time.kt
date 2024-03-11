package io.spamsir.musings.data

import androidx.compose.runtime.saveable.Saver

class Time(var hour: Int = 8, var minute: Int = 0)

val TimeSaver = Saver<Time, List<Int>>(
    save = { listOf(it.hour, it.minute) },
    restore = { Time(it[0], it[1]) }
)