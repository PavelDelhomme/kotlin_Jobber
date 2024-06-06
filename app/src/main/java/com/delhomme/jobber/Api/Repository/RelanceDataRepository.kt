package com.delhomme.jobber.Api.Repository

import android.content.Context
import android.util.Log
import com.delhomme.jobber.Calendrier.Evenement
import com.delhomme.jobber.Calendrier.EventType
import com.delhomme.jobber.Relance.model.Relance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.UUID

class RelanceDataRepository(context: Context) : BaseDataRepository<Relance>(context, "relances") {
    private val listType: Type = object : TypeToken<List<Relance>>() {}.type
    private val gson = Gson()

    override fun updateOrAddItem(mutableItems: MutableList<Relance>, item: Relance) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
        updateEventsForRelance(item)
    }
    fun convertJsonToItems(jsonString: String): MutableList<Relance> {
        return try {
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            Log.e("RelanceDataRepository", "Error parsing JSON", e)
            mutableListOf()
        }
    }

    fun deleteRelance(relanceId: String) {
        allItems?.let { relances ->
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
            title = "Relance de ${relance.entreprise}",
            description = "Relance concernant ${relance.notes}",
            start_time = relance.date_relance.time,
            end_time = relance.date_relance.time + 600000,
            type = EventType.Relance,
            related_id = relance.id,
            entreprise_id = relance.entreprise!!,
            color = "#909090"
        )
        eventRepo.saveItem(event)
    }

    private fun deleteEventForRelance(relance: Relance) {
        val eventRepo = EvenementDataRepository(context)
        eventRepo.deleteEventByRelatedId(relance.id)
    }

    fun loadRelancesForCandidature(candidatureId: String): List<Relance> {
        return findByCondition { it.candidature == candidatureId }
    }
    fun loadRelancesForEntreprise(entrepriseNom: String): List<Relance> {
        return findByCondition { it.entreprise == entrepriseNom }
    }
    fun loadRelancesForContact(contactId: String): List<Relance> {
        return findByCondition { it.contact == contactId }
    }

    // TODO : Implement getTypeRelanceOptions
    fun getTypesRelanceOptions(): List<String> {
        return listOf("---", "Présentiel", "Visioconférence")
    }

    fun getRelancesPerPlateforme(): String {
        val data = getItemsGroupedBy { it.plateforme_utilisee }
            .mapValues { it.value.size }
        return generateHtmlForGraph("Relances par Plateformes", "pie", data)
    }


}