package io.spamsir.musings.data.domain

data class Settings(var userName: String = "", var startTime: Time = Time(), var endTime: Time = Time(), var firstLaunch: Boolean = true)