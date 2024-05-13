package com.delhomme.jobber.models

import java.util.UUID

data class Entreprise(
    val id: String = UUID.randomUUID().toString(),
    val nom: String,
    var contacts: MutableList<Contact> = mutableListOf()
)
