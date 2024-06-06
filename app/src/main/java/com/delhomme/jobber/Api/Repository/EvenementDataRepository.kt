package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Calendrier.Evenement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EvenementDataRepository(context: Context) : BaseDataRepository<Evenement>(context, "evenements") {

    override fun updateOrAddItem(mutableItems: MutableList<Evenement>, item: Evenement) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
    }

    fun findEventByRelatedId(relatedId: String): Evenement? {
        return allItems?.find { it.related_id == relatedId }
    }

    fun deleteEventByRelatedId(relatedId: String) {
        allItems?.let { evenements ->
            val eventToRemove = evenements.firstOrNull { it.related_id == relatedId }
            eventToRemove?.let {
                evenements.remove(it)
                saveItemsToPrefs(evenements)
            }
        }
    }

    fun getEventsOn(date: Date): List<Evenement> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return findByCondition { evenement ->
            format.format(evenement.start_time) == format.format(date)
        }
    }

    // TODO : Implement getTypeEvementOptions
    fun getTypeEvenementOptions(): List<String> {
        return listOf("---", "Candidature", "Relance", "Entretien", "Appel")
    }

    fun deleteEventsByContactId(contactId: String) {
        allItems?.let { evenements ->
            val eventsToRemove = evenements.filter { it.related_id == contactId }
            eventsToRemove.forEach { evenements.remove(it) }
            saveItemsToPrefs(evenements)
        }
    }
}
