package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Model.Evenement
import com.delhomme.jobber.Model.EventType
import java.util.UUID

class AppelDataRepository(context: Context) : BaseDataRepository<Appel>(context, "appels") {
    override fun updateOrAddItem(mutableItems: MutableList<Appel>, item: Appel) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
        updateEventForAppel(item)
    }

    fun deleteAppel(appelId: String) {
        items?.let { appels ->
            val appelToRemove = appels.firstOrNull { it.id == appelId }
            appelToRemove?.let {
                appels.remove(it)
                deleteEventForAppel(it)
                saveItemsToPrefs(appels)
            }
        }
    }

    fun deleteAppelByContactId(contactId: String) {
        items?.let { appels ->
            val toRemove = appels.filter { it.contact_id == contactId }
            appels.removeAll(toRemove)
            toRemove.forEach {
                deleteEventForAppel(it)
            }
            saveItemsToPrefs(appels)
        }
    }

    fun getAppelById(appelId: String): Appel? {
        return items?.find { it.id == appelId }
    }

    private fun updateEventForAppel(appel: Appel) {
        val eventRepo = EvenementDataRepository(context)
        val event = eventRepo.findEventByRelatedId(appel.id) ?: Evenement(
            id = UUID.randomUUID().toString(),
            title = "Appel: ${appel.objet}",
            description = "Appel concernant : ${appel.notes}",
            startTime = appel.date_appel.time,
            endTime = appel.date_appel.time + 600000,
            type = EventType.Appel,
            relatedId = appel.id,
            entrepriseId = appel.entrepriseNom!!,
            color = "#808080"
        )
        eventRepo.saveItem(event)
    }

    private fun deleteEventForAppel(appel: Appel) {
        val eventRepo = EvenementDataRepository(context)
        eventRepo.deleteEventByRelatedId(appel.id)
    }

    fun loadAppelsForCandidature(candidatureId: String): List<Appel> {
        return findByCondition { it.candidature_id == candidatureId }
    }

    fun loadAppelsForContact(contactId: String): List<Appel> {
        return findByCondition { it.contact_id == contactId }
    }
    fun loadAppelsForEntreprise(entrepriseNom: String): List<Appel> {
        return findByCondition { it.entrepriseNom == entrepriseNom }
    }

    fun deleteAppelsByEntrepriseId(entrepriseId: String) {
        items?.removeAll { it.entrepriseNom == entrepriseId }
        saveItemsToPrefs(items ?: mutableListOf())
    }
}