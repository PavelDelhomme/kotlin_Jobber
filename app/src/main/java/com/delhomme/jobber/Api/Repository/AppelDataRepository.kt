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
        allItems?.let { appels ->
            val appelToRemove = appels.firstOrNull { it.id == appelId }
            appelToRemove?.let {
                appels.remove(it)
                deleteEventForAppel(it)
                saveItemsToPrefs(appels)
            }
        }
    }

    private fun updateEventForAppel(appel: Appel) {
        val eventRepo = EvenementDataRepository(context)
        val existingEvent = eventRepo.findByCondition { it.related_id == appel.id }.firstOrNull()
        val event = existingEvent ?: Evenement(
            id = UUID.randomUUID().toString(),
            title = "Appel: ${appel.objet}",
            description = "Appel concernant : ${appel.notes}",
            start_time = appel.date_appel.time,
            end_time = appel.date_appel.time + 600000,
            type = EventType.Appel,
            related_id = appel.id,
            entreprise_id = appel.entrepriseNom ?: "",
            color = "#808080"
        )
        eventRepo.saveItem(event)
    }

    private fun deleteEventForAppel(appel: Appel) {
        val eventRepo = EvenementDataRepository(context)
        eventRepo.deleteEventByRelatedId(appel.id)
    }

    fun loadAppelsForCandidature(candidatureId: String): List<Appel> {
        return findByCondition { it.candidature == candidatureId }
    }

    fun loadAppelsForContact(contactId: String): List<Appel> {
        return findByCondition { it.contact == contactId }
    }

    fun loadAppelsForEntreprise(entrepriseNom: String): List<Appel> {
        return findByCondition { it.entrepriseNom == entrepriseNom }
    }

    fun deleteAppelsByEntrepriseId(entrepriseId: String) {
        allItems?.removeAll { it.entrepriseNom == entrepriseId }
        saveItemsToPrefs(allItems ?: mutableListOf())
    }

    fun getAppelsLast7DaysDatas(dayOffset: Int): String {
        val data = getLast7DaysData(dayOffset) { appel -> appel.date_appel }
        return generateHtmlForGraph("Appels des 7 derniers jours", "bar", data)
    }

    fun deleteAppelsByContactId(contactId: String) {
        allItems?.let { appels ->
            val appelsToRemove = appels.filter { it.contact == contactId }
            appelsToRemove.forEach {
                deleteEventForAppel(it)
                appels.remove(it)
            }
            saveItemsToPrefs(appels)
        }
    }
}