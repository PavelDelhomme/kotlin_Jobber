package com.delhomme.jobber.Calendrier

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val type: EventType,
    val relatedId: String,
    val entrepriseId: String
)
