package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Model.Evenement

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
        return items?.find { it.relatedId == relatedId }
    }

    fun deleteEventByRelatedId(relatedId: String) {
        items?.let { evenements ->
            val eventToRemove = evenements.firstOrNull { it.relatedId == relatedId }
            eventToRemove?.let {
                evenements.remove(it)
                saveItemsToPrefs(evenements)
            }
        }
    }
}
