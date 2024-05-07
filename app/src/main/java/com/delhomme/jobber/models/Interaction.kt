package com.delhomme.jobber.models

data class Interaction(
    val id: String,
    val type: String,
    val candidatureInstance: Candidature? = null,
    val entrepriseInstance: Entreprise? = null,
    val contactInstance: Contact? = null
)
