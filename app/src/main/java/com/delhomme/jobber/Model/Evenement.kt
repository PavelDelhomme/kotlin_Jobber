package com.delhomme.jobber.Model

data class Evenement(
    val id: String,
    var title: String,
    var description: String,
    var startTime: Long,
    var endTime: Long,
    val type: EventType,
    val relatedId: String,
    val entrepriseId: String,
    var color: String = "#FFFFFF"
)
