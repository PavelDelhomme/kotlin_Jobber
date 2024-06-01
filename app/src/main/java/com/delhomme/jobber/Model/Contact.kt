package com.delhomme.jobber.Model

import java.util.UUID

data class Contact(
    val id : String = UUID.randomUUID().toString(),
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val entreprise: String,
    val appelsIds: MutableList<String> = mutableListOf(),
    val candidatureIds: MutableList<String>? = mutableListOf(),
) {
    fun getFullName(): String = "$prenom $nom"
    fun getFullNameEntreprise(entreprises: List<Entreprise>): String {
        val entrepriseNom = entreprises.find { it.nom == this.entreprise }?.nom ?: "Entreprise inconnue"
        return "$prenom $nom $entrepriseNom"
    }
}
