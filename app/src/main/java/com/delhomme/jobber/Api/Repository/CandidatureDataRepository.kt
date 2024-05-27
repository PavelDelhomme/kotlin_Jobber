package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Calendrier.EventType
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Model.Evenement
import java.util.UUID

class CandidatureDataRepository(context: Context) : BaseDataRepository<Candidature>(context, "candidatures") {

    override fun updateOrAddItem(mutableItems: MutableList<Candidature>, item: Candidature) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
        updateEventsForCandidature(item)
    }

    fun deleteCandidature(candidatureId: String) {
        items?.let { candidatures ->
            val candidatureToRemove = candidatures.firstOrNull { it.id == candidatureId }
            candidatureToRemove?.let {
                candidatures.remove(it)
                deleteEventForCandidature(it)
                saveItemsToPrefs(candidatures)
            }
        }
    }

    private fun updateEventsForCandidature(candidature: Candidature) {
        val eventRepo = EvenementDataRepository(context)
        val event = eventRepo.findEventByRelatedId(candidature.id) ?: Evenement(
            id = UUID.randomUUID().toString(),
            title = "Candidature pou ${candidature.titre_offre} chez ${candidature.date_candidature}",
            description = "Candidature concernant ${candidature.notes}",
            startTime = candidature.date_candidature.time,
            endTime = candidature.date_candidature.time + 600000,
            type = EventType.Candidature,
            relatedId = candidature.id,
            entrepriseId = candidature.entrepriseNom,
            color = "#202020"
        )
        eventRepo.saveItem(event)
    }

    private fun deleteEventForCandidature(candidature: Candidature) {
        val eventRepo = EvenementDataRepository(context)
        eventRepo.deleteEventByRelatedId(candidature.id)
    }
}
