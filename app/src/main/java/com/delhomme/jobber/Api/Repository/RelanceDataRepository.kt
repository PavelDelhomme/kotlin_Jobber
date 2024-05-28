package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Model.EventType
import com.delhomme.jobber.Model.Evenement
import com.delhomme.jobber.Model.Relance
import java.util.UUID

class RelanceDataRepository(context: Context) : BaseDataRepository<Relance>(context, "relances") {
    override fun updateOrAddItem(mutableItems: MutableList<Relance>, item: Relance) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
        updateEventsForRelance(item)
    }

    fun deleteRelance(relanceId: String) {
        items?.let { relances ->
            val relanceToRemove = relances.firstOrNull { it.id == relanceId }
            relanceToRemove?.let {
                relances.remove(it)
                deleteEventForRelance(it)
                saveItemsToPrefs(relances)
            }
        }
    }

    private fun updateEventsForRelance(relance: Relance) {
        val eventRepo = EvenementDataRepository(context)
        val event = eventRepo.findEventByRelatedId(relance.id) ?: Evenement(
            id = UUID.randomUUID().toString(),
            title = "Relance de ${relance.entrepriseNom}",
            description = "Relance concernant ${relance.notes}",
            startTime = relance.date_relance.time,
            endTime = relance.date_relance.time + 600000,
            type = EventType.Relance,
            relatedId = relance.id,
            entrepriseId = relance.entrepriseNom!!,
            color = "#909090"
        )
        eventRepo.saveItem(event)
    }

    private fun deleteEventForRelance(relance: Relance) {
        val eventRepo = EvenementDataRepository(context)
        eventRepo.deleteEventByRelatedId(relance.id)
    }

}