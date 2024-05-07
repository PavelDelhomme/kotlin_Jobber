package com.delhomme.jobber.models

data class Entreprise(
    val id: String,
    val nom: String,
    val localisation: String?,
    val secteurActivite: String?,
    val description: String?,
    val email: String?,
    val siteEntreprise: String?
)
