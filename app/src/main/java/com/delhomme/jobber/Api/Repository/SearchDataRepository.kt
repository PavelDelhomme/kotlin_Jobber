package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.Model.Evenement
import com.delhomme.jobber.Model.Relance

class SearchDataRepository(context: Context) {
    private val candidatureDataRepository = CandidatureDataRepository(context)
    private val contactDataRepository = ContactDataRepository(context)
    private val appelDataRepository = AppelDataRepository(context)
    private val entretienDataRepository = EntretienDataRepository(context)
    private val relanceDataRepository = RelanceDataRepository(context)
    private val evenementDataRepository = EvenementDataRepository(context)

    fun search(query: String?): List<Any> {
        return listOf(
            searchCandidatures(query),
            searchContacts(query),
            searchAppels(query),
            searchEntretiens(query),
            searchRelances(query),
            searchEvenements(query)
        ).flatten()
    }

    private fun searchCandidatures(query: String?): List<Candidature> =
        candidatureDataRepository.getItems().filter {
            it.titre_offre.contains(query ?: "", ignoreCase = true) ||
                    it.notes?.contains(query ?: "", ignoreCase = true) == true
        }

    private fun searchContacts(query: String?): List<Contact> =
        contactDataRepository.getItems().filter {
            it.getFullName().contains(query ?: "", ignoreCase = true) ||
                    it.email.contains(query ?: "", ignoreCase = true) ||
                    it.entreprise.contains(query ?: "", ignoreCase = true) ||
                    it.telephone.contains(query ?: "", ignoreCase = true)
        }

    private fun searchAppels(query: String?): List<Appel> =
        appelDataRepository.getItems().filter {
            it.objet.contains(query ?: "", ignoreCase = true) ||
                    it.notes.contains(query ?: "", ignoreCase = true) ||
                    it.entrepriseNom!!.contains(query ?: "", ignoreCase = true)
        }

    private fun searchEntretiens(query: String?): List<Entretien> =
        entretienDataRepository.getItems().filter {
            it.entrepriseNom.contains(query ?: "", ignoreCase = true) ||
                    it.notes_pre_entretien?.contains(query ?: "", ignoreCase = true) == true ||
                    it.notes_post_entretien?.contains(query ?: "", ignoreCase = true) == true
        }


    private fun searchRelances(query: String?): List<Relance> =
        relanceDataRepository.getItems().filter {
            it.entreprise.contains(query ?: "", ignoreCase = true) ||
                    it.notes?.contains(query ?: "", ignoreCase = true) == true ||
                    it.plateforme_utilisee.contains(query ?: "", ignoreCase = true)
        }

    private fun searchEvenements(query: String?): List<Evenement> =
        evenementDataRepository.getItems().filter {
            it.title.contains(query ?: "", ignoreCase = true) ||
                    it.description.contains(query ?: "", ignoreCase = true) ||
                    it.type.toString().contains(query ?: "", ignoreCase = true)
        }

    fun getCandidatureById(id: String): Candidature? {
        return candidatureDataRepository.getItems().find { it.id == id }
    }

    fun getContactById(id: String): Contact? {
        return contactDataRepository.getItems().find { it.id == id }
    }
}
