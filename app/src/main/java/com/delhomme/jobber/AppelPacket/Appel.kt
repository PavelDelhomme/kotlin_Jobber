package com.delhomme.jobber.AppelPacket

import com.delhomme.jobber.ContactPacket.Contact
import com.delhomme.jobber.EntreprisePacket.Entreprise

data class Appel(
    val id: String,
    val date: String,
    val description: String,
    val contactInstance: Contact,
    val entrepriseInstance: Entreprise

)
