package com.delhomme.jobber.Model

import java.util.Date
import java.util.UUID

data class Appel(
    val id: String = UUID.randomUUID().toString(),
    val candidature_id: String? = null,
    val contact_id: String? = null,
    val entrepriseNom: String?,
    var date_appel: Date,
    var objet: String,
    var archivee: Boolean = false,
    var notes: String,
)
