package com.delhomme.jobber.models

data class Evenement(
    val id: String,
    val type: String,
    val date: String,
    val titre: String,
    val description: String,
    val referenceId: String?
)
