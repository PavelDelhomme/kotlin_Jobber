package com.delhomme.jobber.models

data class Appel(
    val id: String,
    val date: String,
    val description: String,
    val contactInstance: Contact,
    val entrepriseInstance: Entreprise
)
