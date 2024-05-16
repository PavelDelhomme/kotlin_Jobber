package com.delhomme.jobber.models

import java.util.Date
import java.util.UUID

data class Relance(
    val id: String = UUID.randomUUID().toString(),
    val date_relance: Date,
    val plateformeUtilisee: String,
    val entrepriseId: String,
    val contactId: String?,
    val candidatureId: String,
    val notes: String?,

)