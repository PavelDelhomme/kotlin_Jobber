package com.delhomme.jobber.models

import java.util.UUID

data class Contact(
    val id : String = UUID.randomUUID().toString(),
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val entreprise: Entreprise,
) {
    fun getFullName(): String {
        return "$prenom $nom"
    }
}
