package com.delhomme.jobber.models

data class Contact(
    val id: Int,
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val entreprise_id: Int
)
