package com.delhomme.jobber.Candidature.model

import com.delhomme.jobber.CandidatureState
import java.util.Date
import java.util.UUID

data class Candidature(
    val id : String = UUID.randomUUID().toString(),
    val titre_offre : String,
    var entrepriseNom : String,
    val type_poste: String,
    val plateforme: String,
    val lieuPoste: String?,
    val date_candidature: Date,
    val notes: String,
    val entretiens: MutableList<String> = mutableListOf(),
    val appels: MutableList<String> = mutableListOf(),
    var relances: MutableList<String> = mutableListOf(),
    var state: CandidatureState,
    val retourPostEntretien: Boolean = false,
)
