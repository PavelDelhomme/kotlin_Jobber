package com.delhomme.jobber.models

import com.delhomme.jobber.CandidaturePacket.Candidature
import com.delhomme.jobber.ContactPacket.Contact
import com.delhomme.jobber.EntreprisePacket.Entreprise

data class Interaction(
    val id: String,
    val type: String,
    val candidatureInstance: Candidature? = null,
    val entrepriseInstance: Entreprise? = null,
    val contactInstance: Contact? = null
)
