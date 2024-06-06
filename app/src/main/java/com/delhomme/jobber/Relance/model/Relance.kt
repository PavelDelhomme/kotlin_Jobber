package com.delhomme.jobber.Relance.model

import java.util.Date
import java.util.UUID

data class Relance(
    val id: String = UUID.randomUUID().toString(),
    val date_relance: Date,
    val plateforme_utilisee: String,
    val entreprise: String,
    val contact: String?,
    val candidature: String,
    val notes: String?,

    )