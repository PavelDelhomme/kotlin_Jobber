package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Model.Evenement
import com.delhomme.jobber.Model.EventType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

class CandidatureDataRepository(context: Context) : BaseDataRepository<Candidature>(context, "candidatures") {

    private lateinit var entrepriseRepository: EntrepriseDataRepository
    private lateinit var candidatureRepository: CandidatureDataRepository

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
            title = "Candidature pour ${candidature.titre_offre} chez ${candidature.date_candidature}",
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
    fun addOrUpdateCandidature(candidature: Candidature) {
        val index = items?.indexOfFirst { it.id == candidature.id }
        if (index != null && index != -1) {
            items!![index] = candidature
        } else {
            items?.add(candidature)
        }
        saveItemsToPrefs(items!!)
    }

    fun getTypePosteOptions(): List<String> {
        return listOf("---", "CDD", "CDI", "Freelance", "Intérim", "Alternance")
    }

    fun getPlateformeOptions(): List<String> {
        return listOf(
            "---",
            "HelloWork",
            "LinkedIn",
            "Indeed",
            "Welcome To The Jungle",
            "SpaceMonk",
            "Jobteaser",
            "Monster",
            "Keljob",
            "RegioJob",
            "bretagne-alternance",
            "Ouest-France Emploi",
            "Meteojob",
            "Jooble",
            "APEC",
            "Talent.io",
            "Téléphone",
            "Email",
            "Sur place",
            "WhatsApp",
            "Autre"
        )
    }

    fun loadCandidaturesForContact(contactId: String): List<Candidature> {
        return loadItemsWhereCollectionContains({ it.contacts }, contactId)
    }
    fun loadCandidaturesForEntreprise(entrepriseNom: String): List<Candidature> {
        return loadRelatedItemsById2({ it.entrepriseNom }, entrepriseNom)
    }

    fun addEntretienToCandidature(candidatureId: String, entretienId: String) {
        items?.find { it.id == candidatureId }?.let {
            it.entretiens.add(entretienId)
            saveItemsToPrefs(items!!)
        }
    }

    fun getCandidaturesLast7Days(dayOffset: Int): String {
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.DATE, dayOffset)
        val startDate = endDate.clone() as Calendar
        startDate.add(Calendar.DATE, -7)

        val filteredItems = items?.filter {
            it.date_candidature.after(startDate.time) && it.date_candidature.before(endDate.time)
        }?.groupingBy { SimpleDateFormat("yyyy-MM-dd").format(it.date_candidature) }
            ?.eachCount() ?: emptyMap()

        return generateHtmlForGraph("Candidatures des 7 derniers jours", "", filteredItems)
    }
    fun getCandidaturesPerPlateforme(dayOffset: Int): String {
        return String()
    }
    fun getCandidaturesPerTypePoste(dayOffset: Int): String {
        return String()
    }

    fun getCandidaturesPerCompany(): String {
        val data = getItemsGroupedBy { it.entrepriseNom }
            .mapValues { it.value.size }
        return generateHtmlForGraph("Candidatures par Entreprise", "bar", data)
    }

    fun getCandidaturesPerLocation(): String {
        val data = getItemsGroupedBy { it.lieuPoste }
            .mapValues { it.value.size }
        return generateHtmlForGraph("Candidatures par Lieu", "pie", data)
    }

    fun getCandidaturesPerState(): String {
        val data = getItemsGroupedBy { it.state.toString() }
            .mapValues { it.value.size }
        return generateHtmlForGraph("Candidatures par Etat", "line", data)
    }
}
