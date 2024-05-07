package com.delhomme.jobber.models

data class Entretien(
    val id: String,
    val dateEntretien: String,
    val dateDuRetour: String?,
    val notesPreparation: String?,
    val notesPendant: String?,
    val notesApres: String?,
    val contactInstance: Contact?,
    val candidatureInstance: Candidature
)
