package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Model.EventType
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.Model.Evenement
import java.util.UUID

class EntretienDataRepository(context: Context) : BaseDataRepository<Entretien>(context, "entretiens") {
    override fun updateOrAddItem(mutableItems: MutableList<Entretien>, item: Entretien) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        }
        updateEventForEntretien(item)
    }

    fun deleteEntretien(entretienId: String) {
        items?.let { entretiens ->
            val entretienToRemove = entretiens.firstOrNull { it.id == entretienId }
            entretienToRemove?.let {
                entretiens.remove(it)
                deleteEventForEntretien(it)
                saveItemsToPrefs(entretiens)
            }
        }
    }

    private fun updateEventForEntretien(entretien: Entretien) {
        val eventRepo = EvenementDataRepository(context)
        val event = eventRepo.findEventByRelatedId(entretien.id) ?: Evenement(
            id = UUID.randomUUID().toString(),
            title = "Entretien : ${entretien.entrepriseNom} ${entretien.type}",
            description = "Entretien pour ${entretien.entrepriseNom} de type ${entretien.type} en mode ${entretien.mode} \nNotes de pré entretien : \n${entretien.notes_pre_entretien}\n\nNotes de post-entretien : \n${entretien.notes_post_entretien}",
            startTime = entretien.date_entretien.time,
            endTime = entretien.date_entretien.time + 600000,
            type = EventType.Entretien,
            relatedId = entretien.id,
            entrepriseId = entretien.entrepriseNom,
            color = "#909090"
        )
        eventRepo.saveItem(event)
    }

    private fun deleteEventForEntretien(entretien: Entretien) {
        val eventRepo = EvenementDataRepository(context)
        eventRepo.deleteEventByRelatedId(entretien.id)
    }
}