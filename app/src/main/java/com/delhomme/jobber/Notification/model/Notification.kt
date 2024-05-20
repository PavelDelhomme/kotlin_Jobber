package com.delhomme.jobber.Notification.model

import java.util.Date
import java.util.UUID

data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val titre: String,
    val message: String,
    val date: Date
)
