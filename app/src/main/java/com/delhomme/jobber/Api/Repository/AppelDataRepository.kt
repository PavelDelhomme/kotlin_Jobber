package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Calendrier.EventType
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Model.Evenement
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
}