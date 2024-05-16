package com.delhomme.jobber

import com.delhomme.jobber.models.Entreprise

object EntrepriseManager {
    private val entreprises = mutableListOf<Entreprise>()

    fun getOrCreateEntreprise(nom: String): Entreprise {
        return entreprises.find { it.nom == nom } ?: run {
            val newEntreprise = Entreprise(nom = nom, contactIds = mutableListOf())
            entreprises.add(newEntreprise)
            newEntreprise
        }
    }
}