package com.delhomme.jobber.Calendrier

import com.delhomme.jobber.Calendrier.EventType

data class Evenement(
    val id: String,
    var title: String,
    var description: String,
    var start_time: Long,
    var end_time: Long,
    val type: EventType,
    val related_id: String,
    val entreprise_id: String,
    var color: String = "#FFFFFF"
)
