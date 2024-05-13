package com.delhomme.jobber.models

import java.util.Date
import java.util.UUID

data class Candidature(
    val id : String = UUID.randomUUID().toString(),
    val titre_offre : String,
    var entreprise : Entreprise,
    val type_poste: String,
    val plateforme: String,
    val date_candidature: Date,
    val etat: String,
)
