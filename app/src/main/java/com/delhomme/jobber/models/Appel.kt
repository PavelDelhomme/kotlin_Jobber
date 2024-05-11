package com.delhomme.jobber.models

import java.util.Date

data class Appel(
    val id: Int,
    val contact_id: Int,
    val entreprise_id: Int,
    val date_appel: Date,
    val objet: String,
    val notes: String,
)
