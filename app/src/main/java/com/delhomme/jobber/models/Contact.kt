package com.delhomme.jobber.models

import java.util.UUID

data class Contact(
    val id : String = UUID.randomUUID().toString(),
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val entrepriseId: String,
    val appels: MutableList<Appel>? = mutableListOf(),
    val candidature: MutableList<String>? = mutableListOf(),
) {
    fun getFullName(): String = "$prenom $nom"
    fun getFullNameEntreprise(entreprises: List<Entreprise>): String {
        val entrepriseNom = entreprises.find { it.id == this.entrepriseId }?.nom ?: "Entreprise inconnue"
        return "$prenom $nom $entrepriseNom"
    }
}
