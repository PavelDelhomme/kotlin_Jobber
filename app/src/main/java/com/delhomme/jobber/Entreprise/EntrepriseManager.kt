package com.delhomme.jobber.Entreprise

import com.delhomme.jobber.Entreprise.model.Entreprise

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