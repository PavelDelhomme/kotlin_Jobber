package com.delhomme.jobber.models

import java.util.Date
import java.util.UUID

data class Entretien(
    val id: String = UUID.randomUUID().toString(),
    val entreprise_id: String,
    val contact_id: String? = null,
    val candidature_id: String? = null,
    val date_entretien: Date,
    val type: String, // (RH, technique, autre)
    val mode: String, // (présentiel, visioconférence, téléphone)
    val notes_pre_entretien: String? = null,
    val notes_post_entretien: String? = null,
)
