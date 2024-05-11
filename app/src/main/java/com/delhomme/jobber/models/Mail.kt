package com.delhomme.jobber.models

import com.delhomme.jobber.ContactPacket.Contact
import com.delhomme.jobber.EntreprisePacket.Entreprise

data class Mail(
    val id: String,
    val date: String,
    val objet: String,
    val contenu: String,
    val fichier: String?,
    val contactInstance: Contact,
    val entrepriseInstance: Entreprise
)

