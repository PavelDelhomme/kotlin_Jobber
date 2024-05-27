package com.delhomme.jobber.Model

data class Entreprise(
    val nom: String,
    var contactIds: MutableList<String> = mutableListOf(),
    var relanceIds: MutableList<String> = mutableListOf(),
    var entretiens: MutableList<String> = mutableListOf(),
    var candidatureIds: MutableList<String> = mutableListOf(),
    var eventIds: MutableList<String> = mutableListOf()
) {
    override fun toString(): String = nom
}
