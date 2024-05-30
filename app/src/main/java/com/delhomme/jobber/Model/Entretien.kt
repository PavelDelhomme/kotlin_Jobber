package com.delhomme.jobber.Model

import java.util.Date
import java.util.UUID

data class Entretien(
    val id: String = UUID.randomUUID().toString(),
    val entrepriseNom: String,
    val contact_id: String? = null,
    val contact: Contact?,
    val candidature_id: String,
    val date_entretien: Date,
    val type: String, // (RH, technique, autre)
    val mode: String, // (présentiel, visioconférence, téléphone)
    val notes_pre_entretien: String? = null,
    var archivee: Boolean = false,
    val notes_post_entretien: String? = null,
)
