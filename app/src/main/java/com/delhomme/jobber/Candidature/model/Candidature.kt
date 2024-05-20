package com.delhomme.jobber.Candidature.model

import com.delhomme.jobber.CandidatureState
import java.util.Date
import java.util.UUID

data class Candidature(
    val id : String = UUID.randomUUID().toString(),
    val titre_offre: String,
    val entrepriseNom: String,
    val date_candidature: Date,
    val plateforme: String,
    val type_poste: String,
    val lieuPoste: String,
    var state: CandidatureState,
    val notes: String,
    val retourPostEntretien: Boolean = false,
    val entretiens: MutableList<String> = mutableListOf(),
    val appels: MutableList<String> = mutableListOf(),
    var relances: MutableList<String> = mutableListOf(),
    var etatManuel: Boolean = false // Ajoutez ce champ
)
