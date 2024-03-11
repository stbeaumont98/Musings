package io.spamsir.musings.data

data class Settings(var userName: String = "", var startTime: Time = Time(), var endTime: Time = Time(), var firstLaunch: Boolean = true)