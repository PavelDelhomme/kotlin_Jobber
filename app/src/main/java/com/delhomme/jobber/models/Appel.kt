package com.delhomme.jobber.models

import java.util.Date
import java.util.UUID

data class Appel(
    val id: String = UUID.randomUUID().toString(),
    val candidature_id: Int? = null,
    val contact_id: String? = null,
    val entreprise_id: String?,
    val date_appel: Date,
    val objet: String,
    val notes: String,
)
