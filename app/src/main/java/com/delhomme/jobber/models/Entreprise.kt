package com.delhomme.jobber.models

import java.util.UUID

data class Entreprise(
    val id: String = UUID.randomUUID().toString(),
    val nom: String,
    var contactIds: MutableList<String> = mutableListOf(),
    var entretiens: MutableList<Entretien> = mutableListOf(),
    var candidatureIds: MutableList<String> = mutableListOf()
) {
    override fun toString(): String = nom
}
