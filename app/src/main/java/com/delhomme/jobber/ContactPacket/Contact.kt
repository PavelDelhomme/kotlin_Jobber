package com.delhomme.jobber.ContactPacket

import com.delhomme.jobber.EntreprisePacket.Entreprise

data class Contact(
    val id: String,
    val nom: String,
    val prenom: String,
    val telephone: String,
    val email: String,
    val entrepriseNom: String,
    val entrepriseInstance: Entreprise,
    //val candidatures: List<Candidature>
)
