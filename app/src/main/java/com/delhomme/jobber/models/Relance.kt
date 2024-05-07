package com.delhomme.jobber.models

data class Relance(
    val id: String,
    val notes: String,
    val dateDuRetour: String?,
    val contactInstance: Contact?,
    val entrepriseInstance: Entreprise,
    val candidatureInstance: Candidature,
    val aRecuRetour: Boolean = false,
    val type: String
)
