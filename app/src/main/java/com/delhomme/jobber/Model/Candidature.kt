package com.delhomme.jobber.Model

import com.delhomme.jobber.Utils.CandidatureState
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
    var notes: String,
    val retourPostEntretien: Boolean = false,
    var archivee: Boolean = false,
    val entretiens: MutableList<String> = mutableListOf(),
    val appels: MutableList<String> = mutableListOf(),
    var relances: MutableList<String> = mutableListOf(),
    var etatManuel: Boolean = false,
    var contacts: MutableList<String> = mutableListOf()
)
