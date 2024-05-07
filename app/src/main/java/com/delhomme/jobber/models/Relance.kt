package com.delhomme.jobber.models

import com.delhomme.jobber.CandidaturePacket.Candidature
import com.delhomme.jobber.ContactPacket.Contact
import com.delhomme.jobber.EntreprisePacket.Entreprise

data class Relance(
    val id: String,
    val notes: String,
    val dateDuRetour: String?,
    val contactInstance: Contact?,
    val entrepriseInstance: Entreprise,
    val candidatureInstance: Candidature,
    val aRecuRetour: Boolean = false,
    val type: String
)
