package com.delhomme.jobber.models

import java.util.Date
import java.util.UUID

data class Entretien(
    val id: String = UUID.randomUUID().toString(),
    val candidature_id: String? = null,
    val contact_id: String? = null,
    val entreprise_id: String,
    val entrepriseNom: String? =null,
    val date_entretien: Date,
    val type_entretien: String,
    val style_entretien: String,
    val notes_pre_entretien: String? = null,
    val notes_post_entretien: String? = null,
)
