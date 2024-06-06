package com.delhomme.jobber.Api.Repository

import android.content.Context
import android.util.Log
import com.delhomme.jobber.Calendrier.Evenement
import com.delhomme.jobber.Calendrier.EventType
import com.delhomme.jobber.Entretien.model.Entretien
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.UUID

class EntretienDataRepository(context: Context) : BaseDataRepository<Entretien>(context, "entretiens") {

    private val listType: Type = object : TypeToken<List<Entretien>>() {}.type
    private val gson = Gson()


    fun convertJsonToItems(jsonString: String): MutableList<Entretien> {
        return try {
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            Log.e("EntretienDataRepository", "Error parsing JSON", e)
            mutableListOf()
        }
    }

    override fun updateOrAddItem(mutableItems: MutableList<Entretien>, item: Entretien) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        }
        updateEventForEntretien(item)
    }

    fun deleteEntretien(entretienId: String) {
        allItems?.let { entretiens ->
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
            start_time = entretien.date_entretien.time,
            description = "Entretien pour ${entretien.entrepriseNom} de type ${entretien.type} en mode ${entretien.mode} \nNotes de pré entretien : \n${entretien.notes_pre_entretien}\n\nNotes de post-entretien : \n${entretien.notes_post_entretien}",
            end_time = entretien.date_entretien.time + 600000,
            type = EventType.Entretien,
            related_id = entretien.id,
            entreprise_id = entretien.entrepriseNom,
            color = "#909090"
        )
        eventRepo.saveItem(event)
    }

    private fun deleteEventForEntretien(entretien: Entretien) {
        val eventRepo = EvenementDataRepository(context)
        eventRepo.deleteEventByRelatedId(entretien.id)
    }

    // TODO : Implement getTypeEntretienOptions
    fun getTypeEntretienOptions(): List<String> {
        return listOf("---", "Présentiel", "Visio-conférence")
    }

    // TODO Test de l'implémentation de la récupération de tout les contacts lié à l'entretien
    fun loadEntretiensForContact(contactId: String): List<Entretien> {
        return loadRelatedItemsById2({ it.id }, contactId)
    }

    fun loadEntretiensForEntreprise(entrepriseNom: String): List<Entretien> {
        return loadRelatedItemsById2({ entrepriseNom }, entrepriseNom)
    }
    fun loadEntretiensForCandidature(candidatureId: String): List<Entretien> {
        return loadRelatedItemsById2({ it.candidatureId }, candidatureId)
    }

    fun getEntretiensPerTypeDatas(): String {
        val data = getItemsGroupedBy { it.type }
            .mapValues { it.value.size }
        return generateHtmlForGraph("Entretiens par Type", "doughnut", data)
    }
    fun getEntretiensLast7Days(dayOffset: Int): String {
        val data = getLast7DaysData(dayOffset) { entretien -> entretien.date_entretien }
        return generateHtmlForGraph("Entretiens des Derniers 7 Jours", "line", data)
    }

    fun getEntretiensPerStyleDatas(): String {
        val data = getItemsGroupedBy { it.mode }
            .mapValues { it.value.size }
        return generateHtmlForGraph("Entretiens par Style", "polarArea", data)
    }
}