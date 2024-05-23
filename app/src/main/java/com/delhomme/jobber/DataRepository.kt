package com.delhomme.jobber

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.Calendrier.Evenement
import com.delhomme.jobber.Calendrier.EventType
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.Entreprise.model.Entreprise
import com.delhomme.jobber.Entretien.model.Entretien
import com.delhomme.jobber.Notification.NotificationReceiver
import com.delhomme.jobber.Notification.model.Notification
import com.delhomme.jobber.Relance.model.Relance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class DataRepository(val context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("CandidaturesPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Cache en mémoire
    private var candidatures: List<Candidature>? = null
    private var entreprises: List<Entreprise>? = null
    private var contacts: List<Contact>? = null
    private var appels: List<Appel>? = null
    private var entretiens: List<Entretien>? = null
    private var relances: List<Relance>? = null
    private var notifications: List<Notification>? = null
    private var evenements: List<Evenement>? = null

    val stateColors = arrayOf(
        "#E3F2FD", // CANDIDATEE_ET_EN_ATTENTE
        "#FFCCBC", // EN_ATTENTE_APRES_ENTRETIEN
        "#C8E6C9", // EN_ATTENTE_D_UN_ENTRETIEN
        "#FFF9C4", // FAIRE_UN_RETOUR_POST_ENTRETIEN
        "#D1C4E9", // A_RELANCEE_APRES_ENTRETIEN
        "#FFAB91", // A_RELANCEE
        "#B2EBF2", // RELANCEE_ET_EN_ATTENTE
        "#F8BBD0", // AUCUNE_REPONSE
        "#D7CCC8", // NON_RETENU
        "#BCAAA4", // ERREUR
        "#FFCDD2", // NON_RETENU_APRES_ENTRETIEN
        "#CFD8DC"  // NON_RETENU_SANS_ENTRETIEN
    )


    init {
        loadInitialData()
        generateDefaultEvents()
        checkAndUpdateCandidatureStates()
    }

    private fun loadInitialData() {
        candidatures = loadCandidatures()
        entreprises = loadEntreprises()
        contacts = loadContacts()
        appels = loadAppels()
        entretiens = loadEntretiens()
        relances = loadRelances()
        notifications = loadNotifications()
        evenements = loadEvents()
        Log.d("DataRepository", "DataRepository events loader : $evenements")
    }

    fun getCandidatures(): List<Candidature> {
        return getAllCandidatures().filterNot { it.archivee }
    }
    fun getAllCandidatures() = candidatures ?: emptyList()
    fun getEntreprises() = entreprises ?: emptyList()
    fun getContacts() = contacts ?: emptyList()
    fun getAppels() = appels ?: emptyList()
    fun getEntretiens() = entretiens ?: emptyList()
    fun getRelances() = relances ?: emptyList()

    fun saveCandidature(candidature: Candidature) {
        val mutableCandidature = candidatures?.toMutableList() ?: mutableListOf()
        val index = mutableCandidature.indexOfFirst { it.id == candidature.id }
        if (index != -1) {
            mutableCandidature[index] = candidature // Mise à jour
        } else {
            mutableCandidature.add(candidature) // Ajout
            val entreprise = entreprises?.find { it.nom == candidature.entrepriseNom }
            if (entreprise != null) {
                if (!entreprise.candidatureIds.contains(candidature.id)) {
                    entreprise.candidatureIds.add(candidature.id)
                }
            } else {
                val newEntreprise = Entreprise(
                    nom = candidature.entrepriseNom,
                    candidatureIds = mutableListOf(candidature.id)
                )
                saveEntreprise(newEntreprise)
                val intent_entreprise_list = Intent("ccom.jobber.ENTREPRISE_LIST_UPDATED")
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent_entreprise_list)
            }
        }
        val existingEvent = evenements?.find { it.relatedId == candidature.id }
        if (existingEvent != null) {
            existingEvent.title = "Candidature : ${candidature.titre_offre}"
            existingEvent.description = "Candidature pour ${candidature.entrepriseNom} à ${SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(candidature.date_candidature)}"
            candidature.date_candidature.also { existingEvent.startTime = it.time }
            existingEvent.endTime = candidature.date_candidature.time + 10 * 60 * 1000
            saveEvent(existingEvent)
        } else {
            // Create a new event
            val newEvenement = Evenement(
                id = UUID.randomUUID().toString(),
                title = "Candidature: ${candidature.titre_offre}",
                description = "Candidature for ${candidature.entrepriseNom} at ${SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(candidature.date_candidature)}",
                startTime = candidature.date_candidature.time,
                endTime = candidature.date_candidature.time + 10 * 60 * 1000, // 10 minutes duration
                type = EventType.Candidature,
                relatedId = candidature.id,
                entrepriseId = candidature.entrepriseNom,
                color = "#FFFF00"
            )
            saveEvent(newEvenement)
        }

        candidatures = mutableCandidature
        val jsonString = gson.toJson(candidatures)
        sharedPreferences.edit().putString("candidatures", jsonString).apply()

        updateCandidatureState(candidature)

        val intent_candidature_list = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent_candidature_list)
    }


    fun saveEntreprise(entreprise: Entreprise) {
        val mutableEntreprises = entreprises?.toMutableList() ?: mutableListOf()
        val index = mutableEntreprises.indexOfFirst { it.nom == entreprise.nom }
        if (index != -1) {
            mutableEntreprises[index] = entreprise
        } else {
            mutableEntreprises.add(entreprise)
        }
        entreprises = mutableEntreprises
        val jsonString = gson.toJson(entreprises)
        sharedPreferences.edit().putString("entreprises", jsonString).apply()
    }

    fun saveRelance(relance: Relance) {
        val relances = loadRelances().toMutableList()
        val index = relances.indexOfFirst { it.id == relance.id }
        if (index != -1) {
            relances[index] = relance
        } else {
            relances.add(relance)
            val entreprise = getEntrepriseByNom(relance.entrepriseNom)
            entreprise?.let {
                it.relanceIds = (it.relanceIds ?: mutableListOf()).apply { add(relance.id) }
                saveEntreprise(it)
            }
            val candidature = getCandidatureById(relance.candidatureId)
            candidature?.let {
                it.relances = (it.relances ?: mutableListOf()).apply { add(relance.id) }
                saveCandidature(it)
            }

            if (relance.plateformeUtilisee == "Téléphone") {
                val appel = if (relance.contactId!!.isNotEmpty()) {
                    Appel(date_appel = Date(), objet = "Suivi relance", notes = "Appel suite à relance", entrepriseNom = relance.entrepriseNom, contact_id = relance.contactId, candidature_id = relance.candidatureId)
                } else {
                    Appel(date_appel = Date(), objet = "Suivi relance", notes = "Appel suite à relance", entrepriseNom = relance.entrepriseNom, contact_id = "", candidature_id = relance.candidatureId)
                }
                saveAppel(appel)
            }
            handleEventForRelance(relance, candidature)
            }
        val jsonString = gson.toJson(relances)
        sharedPreferences.edit().putString("relances", jsonString).apply()
    }

    fun handleEventForRelance(relance: Relance, candidature: Candidature?) {
        candidature?.let { cand ->
            val events = loadEvents()
            val existingEvent = events.find { it.relatedId == relance.id }

            if (existingEvent != null) {
                existingEvent.title = "Relance pour : ${candidature.titre_offre}"
                existingEvent.description = "Relance pour ${relance.entrepriseNom} à ${SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(relance.date_relance)}"
                existingEvent.startTime = relance.date_relance.time
                existingEvent.endTime = relance.date_relance.time + 10 * 60 * 1000 // 10 min
                saveEvent(existingEvent)
            } else {
                val newEvenement = Evenement(
                    id = UUID.randomUUID().toString(),
                    title = "Relance pour ${relance.entrepriseNom} à ${SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(relance.date_relance)}",
                    startTime = relance.date_relance.time,
                    endTime = relance.date_relance.time + 10 * 60 * 1000,
                    type = EventType.Relance,
                    relatedId = relance.id,
                    entrepriseId = relance.entrepriseNom,
                    description = "",
                    color = "#FFFF00"
                )
                saveEvent(newEvenement)
            }
        }
    }

    fun saveEntretien(entretien: Entretien) {
        val entretiens = loadEntretiens().toMutableList()
        val index = entretiens.indexOfFirst { it.id == entretien.id }
        if (index != -1) {
            entretiens[index] = entretien
        } else {
            entretiens.add(entretien)
        }

        val candidature = getCandidatureById(entretien.candidature_id)
        candidature?.let {
            if (!it.entretiens.contains(entretien.id)) {
                it.entretiens.add(entretien.id)
                saveCandidature(it)
                updateCandidatureState(it)
            }
        }

        val contact = getContactById(entretien.contact_id)
        contact?.let {
            if (it.candidatureIds?.contains(entretien.candidature_id) == false) {
                it.candidatureIds?.add(entretien.candidature_id)
                saveContact(it)
            }
        }

        val newEvenement = candidature?.entrepriseNom?.let {
            candidature?.id?.let { it1 ->
                Evenement(
                    id = UUID.randomUUID().toString(),
                    title = "Candidature: ${candidature?.titre_offre}",
                    description = "Candidature for ${candidature?.entrepriseNom} at ${SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(candidature.date_candidature)}",
                    startTime = candidature.date_candidature.time,
                    endTime = candidature.date_candidature.time + 10 * 60 * 1000, // 10 minutes duration
                    type = EventType.Candidature,
                    relatedId = it1,
                    entrepriseId = it,
                    color = "#FFFF00"
                )
            }
        }
        if (newEvenement != null) {
            saveEvent(newEvenement)
        }


        val jsonString = gson.toJson(entretiens)
        sharedPreferences.edit().putString("entretiens", jsonString).apply()
    }


    fun saveContact(contact: Contact) {
        val contacts = loadContacts().toMutableList()
        val index = contacts.indexOfFirst { it.id == contact.id }
        if (index != -1) {
            contacts[index] = contact
        } else {
            contacts.add(contact)
        }
        val jsonString = gson.toJson(contacts)
        sharedPreferences.edit().putString("contacts", jsonString).apply()
    }

    fun saveAppel(appel: Appel) {
        val appels = loadAppels().toMutableList()
        val index = appels.indexOfFirst { it.id == appel.id }
        if (index != -1) {
            appels[index] = appel
        } else {
            appels.add(appel)
        }

        val candidature = appel.candidature_id?.let { getCandidatureById(it) }
        candidature?.let {
            updateCandidatureState(it)
        }
        if (candidature != null) {
            val event = evenements?.find { it.relatedId == appel.id }
            if (event != null) {
                updateEvent(event, appel, candidature)
            } else {
                createEventForAppel(appel, candidature)
            }
        }
        val jsonString = gson.toJson(appels)
        sharedPreferences.edit().putString("appels", jsonString).apply()
    }
    // todo : Faire de l'héritage ou polymorphisme
    fun deleteCandidature(candidatureId: String) {
        val candidatures = loadCandidatures().toMutableList()
        val candidature = candidatures.find { it.id == candidatureId }

        if (candidature != null) {
            val entreprise = getEntrepriseByNom(candidature?.entrepriseNom)
            entreprise?.candidatureIds?.remove(candidatureId)

            entreprise?.let {
                saveEntreprise(it)
            }

            candidatures.remove(candidature)
            val jsonString = gson.toJson(candidatures)
            sharedPreferences.edit().putString("candidatures", jsonString).apply()

            val intent = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            Log.d("DataRepository", "Broadcast sent: com.jobber.CANDIDATURE_LIST_UPDATED")
        } else {
            Log.e("DataRepository", "Candidature wtih ID $candidatureId not found")
        }

    }


    fun deleteContact(contactId: String) {
        val currentContact = loadContacts().toMutableList()
        val filteredContact = currentContact.filter { it.id != contactId }
        val jsonString = gson.toJson(filteredContact)
        sharedPreferences.edit().putString("contacts", jsonString).apply()
    }

    fun deleteAppel(appelId: String) {
        val currentAppel = loadAppels().toMutableList()
        val filteredAppel = currentAppel.filter { it.id != appelId }
        val appelGet = getAppelById(appelId)
        val candidature = appelGet?.candidature_id?.let { getCandidatureById(it) }
        candidature?.let {
            updateCandidatureState(it)
        }
        val jsonString = gson.toJson(filteredAppel)
        sharedPreferences.edit().putString("appels", jsonString).apply()
    }

    fun deleteEntreprise(entrepriseNom: String) {
        val currentEntreprise = loadEntreprises().toMutableList()
        val filteredEntreprise = currentEntreprise.filter { it.nom != entrepriseNom }
        val jsonString = gson.toJson(filteredEntreprise)
        sharedPreferences.edit().putString("entreprises", jsonString).apply()
    }

    fun deleteRelance(relanceId: String) {
        val currentRelance = loadRelances().toMutableList()
        val filteredRelance = currentRelance.filter { it.id != relanceId }
        val relanceGet = getRelanceById(relanceId)
        val candidature = relanceGet?.candidatureId?.let { getCandidatureById(it) }
        candidature?.let {
            updateCandidatureState(it)
        }
        val jsonString = gson.toJson(filteredRelance)
        sharedPreferences.edit().putString("relances", jsonString).apply()
    }

    fun deleteEntretien(entretienId: String) {
        val currentEntretien = loadEntretiens().toMutableList()
        val filteredEntretien = currentEntretien.filter { it.id != entretienId }
        val entretienGet = getEntretienById(entretienId)
        val candidature = entretienGet?.candidature_id?.let { getCandidatureById(it) }
        candidature?.let {
            updateCandidatureState(it)
        }
        val jsonString = gson.toJson(filteredEntretien)
        sharedPreferences.edit().putString("entretiens", jsonString).apply()
    }

    fun addContactToEntreprise(contactId: String, entrepriseNom: String) {
        val entreprise = getEntrepriseByNom(entrepriseNom)
        if (entreprise != null) {
            if (!entreprise.contactIds.contains(contactId)) {
                entreprise.contactIds.add(contactId)
                saveEntreprise(entreprise)
            }
        } else {
            Log.d(
                "DataRepository",
                "addContactToEntreprise : No entreprise found with Nom: $entrepriseNom"
            )
        }
    }

    fun getEntrepriseByNom(nom: String?): Entreprise? {
        Log.d("DataRepository", "Recherche de l'entreprise avec le nom : $nom")
        val result = entreprises?.find { it.nom == nom }
        Log.d("DataRepository", "Résultat de la recherche : $result")
        return result
    }

    fun getCandidatureById(id: String): Candidature? {
        val candidatureString = sharedPreferences.getString("candidatures", "")
        val type = object : TypeToken<List<Candidature>>() {}.type
        val candidatures = gson.fromJson<List<Candidature>>(candidatureString, type) ?: return null
        return candidatures.find { it.id == id }
    }

    fun getAppelById(id: String): Appel? {
        val appelString = sharedPreferences.getString("appels", "")
        val type = object : TypeToken<List<Appel>>() {}.type
        val appels = gson.fromJson<List<Appel>>(appelString, type) ?: return null
        return appels.find { it.id == id }
    }

    fun getEntretienById(id: String): Entretien? {
        val entretienString = sharedPreferences.getString("entretiens", "")
        val type = object : TypeToken<List<Entretien>>() {}.type
        val entretiens = gson.fromJson<List<Entretien>>(entretienString, type) ?: return null
        return entretiens.find { it.id == id }
    }

    fun getRelanceById(id: String): Relance? {
        return loadRelances().find { it.id == id }
    }

    fun getContactById(id: String?): Contact? {
        val contactString = sharedPreferences.getString("contacts", "")
        val type = object : TypeToken<List<Contact>>() {}.type
        val contacts = gson.fromJson<List<Contact>>(contactString, type) ?: return null
        return contacts.find { it.id == id }
    }

    fun reloadEntreprises() {
        val jsonString = sharedPreferences.getString("entreprises", null)
        if (jsonString != null) {
            val type = object : TypeToken<List<Entreprise>>() {}.type
            gson.fromJson<List<Entreprise>>(jsonString, type) ?: emptyList()
        }
    }

    fun reloadEntretiens() {
        val jsonString = sharedPreferences.getString("entretiens", null)
        if (jsonString != null) {
            val type = object : TypeToken<List<Entretien>>() {}.type
            gson.fromJson<List<Entretien>>(jsonString, type) ?: emptyList()
        }
    }

    fun reloadRelances() {
        val jsonString = sharedPreferences.getString("relances", null)
        if (jsonString != null) {
            val type = object : TypeToken<List<Relance>>() {}.type
            gson.fromJson<List<Relance>>(jsonString, type) ?: emptyList()
        }
    }

    fun reloadAppels() {
        val jsonString = sharedPreferences.getString("appels", null)
        if (jsonString != null) {
            val type = object : TypeToken<List<Relance>>() {}.type
            gson.fromJson<List<Appel>>(jsonString, type) ?: emptyList()
        }
    }

    fun reloadContacts() {
        val jsonString = sharedPreferences.getString("contacts", null)
        if (jsonString != null) {
            val type = object : TypeToken<List<Contact>>() {}.type
            gson.fromJson<List<Contact>>(jsonString, type) ?: emptyList()
        }
    }

    fun reloadCandidatures() {
        val jsonString = sharedPreferences.getString("candidatures", null)
        if (jsonString != null) {
            val type = object : TypeToken<List<Candidature>>() {}.type
            gson.fromJson<List<Candidature>>(jsonString, type) ?: emptyList()
        }
    }

    private fun loadCandidatures(): List<Candidature> {
        val jsonString = sharedPreferences.getString("candidatures", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Candidature>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    private fun loadRelances(): List<Relance> {
        val jsonString = sharedPreferences.getString("relances", null)
        return if (!jsonString.isNullOrEmpty()) {
            val type = object : TypeToken<List<Relance>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    private fun loadEntreprises(): List<Entreprise> {
        val jsonString = sharedPreferences.getString("entreprises", null)
        val entreprises = if (jsonString != null) {
            val type = object : TypeToken<List<Entreprise>>() {}.type
            gson.fromJson<List<Entreprise>>(jsonString, type)
        } else {
            emptyList()
        }
        Log.d("DataRepository", "Entreprises chargées : $entreprises")
        return entreprises
    }

    private fun loadAppels(): List<Appel> {
        val appelsJson = sharedPreferences.getString("appels", null)
        return if (appelsJson != null) {
            val type = object : TypeToken<List<Appel>>() {}.type
            gson.fromJson(appelsJson, type)
        } else {
            emptyList()
        }
    }

    private fun loadEntretiens(): List<Entretien> {
        val entretiensJson = sharedPreferences.getString("entretiens", null)
        return if (entretiensJson != null) {
            val type = object : TypeToken<List<Entretien>>() {}.type
            gson.fromJson(entretiensJson, type)
        } else {
            emptyList()
        }
    }

    private fun loadContacts(): List<Contact> {
        val contactsJson = sharedPreferences.getString("contacts", null)
        if (contactsJson != null) {
            val type = object : TypeToken<List<Contact>>() {}.type
            val contacts = gson.fromJson<List<Contact>>(contactsJson, type)
            contacts.forEach { contact ->
                Log.d(
                    "DataRepository",
                    "Loaded contact ${contact.id} with entreprise ID : ${contact.entrepriseNom}"
                )
            }
            return contacts
        } else {
            return emptyList()
        }
    }
    fun loadAppelsForContact(contact_id: String): List<Appel> {
        return loadAppels().filter { it.contact_id == contact_id }
    }

    fun loadAppelsForEntreprise(entrepriseNom: String): List<Appel> {
        return loadAppels().filter { it.entrepriseNom == entrepriseNom }
    }

    fun loadAppelsForCandidature(candidature_id: String): List<Appel> {
        return loadAppels().filter { it.candidature_id == candidature_id }
    }

    fun loadCandidaturesForEntreprise(entrepriseNom: String): List<Candidature> {
        return loadCandidatures().filter { it.entrepriseNom == entrepriseNom }
    }

    fun loadEntretiensForEntreprise(entrepriseNom: String): List<Entretien> {
        return loadEntretiens().filter { it.entrepriseNom == entrepriseNom }
    }

    fun loadEntretiensForCandidature(candidatureId: String): List<Entretien> {
        return loadEntretiens().filter { it.candidature_id == candidatureId }
    }

    fun loadContactsForEntreprise(entrepriseNom: String?): List<Contact> {
        return loadContacts().filter { it.entrepriseNom == entrepriseNom }
    }

    fun loadRelancesForEntreprise(entrepriseId: String?): List<Relance> {
        return loadRelances().filter { it.entrepriseNom == entrepriseId }
    }

    fun loadRelancesForCandidature(candidatureId: String): List<Relance> {
        val allRelances = loadRelances()
        return allRelances?.filter { it.candidatureId == candidatureId } ?: emptyList()
    }

    fun getOrCreateEntreprise(companyName: String): Entreprise {
        val existing = loadEntreprises().find { it.nom == companyName }
        if (existing != null) {
            return existing
        }

        val newEntreprise = Entreprise(
            nom = companyName,
            contactIds = mutableListOf(),
            entretiens = mutableListOf()
        )
        saveEntreprise(newEntreprise)
        return newEntreprise
    }

    fun getOrCreateContact(nom: String, prenom: String, entrepriseNom: String): Contact {
        val existing =
            loadContacts().find { it.nom == nom && it.prenom == prenom && it.entrepriseNom == entrepriseNom }
        if (existing != null) {
            return existing
        }

        val newContact = Contact(
            id = UUID.randomUUID().toString(),
            nom = nom,
            prenom = prenom,
            email = "",
            telephone = "",
            entrepriseNom = entrepriseNom,
            appelsIds = mutableListOf(),
            candidatureIds = mutableListOf()
        )
        saveContact(newContact)
        addContactToEntreprise(newContact.id, entrepriseNom)
        return newContact
    }

    fun getContactsForEntreprise(entrepriseNom: String): List<Contact>? {
        return contacts?.filter { it.entrepriseNom == entrepriseNom }
    }

    fun editCandidature(candidatureId: String, newTitre: String, newEtat: CandidatureState, newNotes: String, newPlateforme: String, newTypePoste: String, newLieuPoste: String, newEntrepriseNom: String, newDate: Date, newEntretiens: MutableList<String>, newAppelsIds: MutableList<String>, newRelances: MutableList<String>) {
        val candidatures = loadCandidatures().toMutableList()
        val index = candidatures.indexOfFirst { it.id == candidatureId }
        if (index != -1) {
            val oldCandidature = candidatures[index]
            val updatedCandidature = oldCandidature.copy(
                titre_offre = newTitre,
                state = newEtat,
                notes = newNotes,
                plateforme = newPlateforme,
                type_poste = newTypePoste,
                lieuPoste = newLieuPoste,
                entrepriseNom = newEntrepriseNom,
                date_candidature = newDate,
                entretiens = newEntretiens,
                appels = newAppelsIds,
                relances = newRelances
            )
            candidatures[index] = updatedCandidature
            val jsonString = gson.toJson(candidatures)
            sharedPreferences.edit().putString("candidatures", jsonString).apply()

            val intent = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }

    fun editEntreprise(entrepriseNom: String, newName: String, newContactIds: MutableList<String>, newRelancesIds: MutableList<String>, newEntretiensIds: MutableList<String>, newCandidaturesIds: MutableList<String>) {
        val entreprises = loadEntreprises().toMutableList()
        val index = entreprises.indexOfFirst { it.nom == entrepriseNom }
        if (index != -1) {
            val oldEntreprise = entreprises[index]
            val updatedEntreprise = oldEntreprise.copy(
                nom = newName,
                contactIds = newContactIds,
                relanceIds = newRelancesIds,
                entretiens = newEntretiensIds,
                candidatureIds = newCandidaturesIds
            )
            entreprises[index] = updatedEntreprise
            val jsonString = gson.toJson(entreprises)
            sharedPreferences.edit().putString("entreprises", jsonString).apply()
        }
    }

    fun editRelance(relanceId: String, newDate: Date, newPlateformeUtilise: String, newEntrepriseNom: String, newContactId: String?, newCandidatureId: String, newNotes: String?) {
        val relances = loadRelances().toMutableList()
        val index = relances.indexOfFirst { it.id == relanceId }
        if (index != -1) {
            val oldRelance = relances[index]
            val updatedRelance = oldRelance.copy(
                date_relance = newDate,
                plateformeUtilisee = newPlateformeUtilise,
                entrepriseNom = newEntrepriseNom,
                contactId = newContactId,
                candidatureId = newCandidatureId,
                notes = newNotes
            )
            relances[index] = updatedRelance
            val jsonString = gson.toJson(relances)
            sharedPreferences.edit().putString("relances", jsonString).apply()
        }
    }

    // TODO : Implement editAppel method into EditAppelActivity
    fun editAppel(appelId: String, newCandidatureId: String, newContactId: String?, newEntrepriseNom: String, newDateAppel: Date, newObjet: String, newNotes: String) {
        val appels = loadAppels().toMutableList()
        val index = appels.indexOfFirst { it.id == appelId }
        if (index != -1) {
            val oldAppel = appels[index]
            val updatedAppel = oldAppel.copy(
                candidature_id = newCandidatureId,
                contact_id = newContactId,
                entrepriseNom = newEntrepriseNom,
                date_appel = newDateAppel,
                objet = newObjet,
                notes = newNotes,
            )
            appels[index] = updatedAppel
            val jsonString = gson.toJson(appels)
            sharedPreferences.edit().putString("appels", jsonString).apply()
        }
    }

    fun editContact(contactId: String, newNom: String, newPrenom: String, newEmail: String, newTelephone: String, newEntrepriseNom: String, newAppelsIds: MutableList<String>, newCandidatureIds: MutableList<String>) {
        val contacts = loadContacts().toMutableList()
        val index = contacts.indexOfFirst { it.id == contactId }
        if (index != -1) {
            val oldContact = contacts[index]
            val updatedContact = oldContact.copy(
                nom = newNom,
                prenom = newPrenom,
                email = newEmail,
                telephone = newTelephone,
                entrepriseNom = newEntrepriseNom,
                appelsIds = newAppelsIds,
                candidatureIds = newCandidatureIds,
            )
            contacts[index] = updatedContact
            val jsonString = gson.toJson(contacts)
            sharedPreferences.edit().putString("contacts", jsonString).apply()
        }
    }

    fun editEntretien(entretienId: String, newEntrepriseNom: String, newContactId: String?, newCandidatureId: String, newDateEntretien: Date, newType: String, newMode: String, newNotesPreEntretien: String?, newNotesPostEntretien: String?) {
        val entretiens = loadEntretiens().toMutableList()
        val index = entretiens.indexOfFirst { it.id == entretienId }
        if (index != -1) {
            val oldEntretien = entretiens[index]
            val updatedEntretien = oldEntretien.copy(
                entrepriseNom = newEntrepriseNom,
                contact_id = newContactId,
                candidature_id = newCandidatureId,
                date_entretien = newDateEntretien,
                type = newType,
                mode = newMode,
                notes_pre_entretien = newNotesPreEntretien,
                notes_post_entretien = newNotesPostEntretien
            )
            entretiens[index] = updatedEntretien
            val jsonString = gson.toJson(entretiens)
            sharedPreferences.edit().putString("entretiens", jsonString).apply()
        }
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

    // TODO : Implement getTypeEntretienOptions
    fun getTypeEntretienOptions(): List<String> {
        return listOf("---", "Présentiel", "Visio-conférence")
    }

    // TODO : Implement getTypeRelanceOptions
    fun getTypesRelanceOptions(): List<String> {
        return listOf("---", "Présentiel", "Visioconférence")
    }

    // TODO : Implement getTypeEvementOptions
    fun getTypeEvenementOptions(): List<String> {
        return listOf("---", "Candidature", "Relance", "Entretien", "Appel")
    }

    fun updateEntrepriseName(oldName: String, newName: String) {
        // Mettre à jour le nom dans les candidatures
        candidatures = candidatures?.map { candidature ->
            if (candidature.entrepriseNom == oldName) {
                candidature.copy(entrepriseNom = newName)
            } else {
                candidature
            }
        }

        // Mettre à jour le nom dans les contacts
        contacts = contacts?.map { contact ->
            if (contact.entrepriseNom == oldName) {
                contact.copy(entrepriseNom = newName)
            } else {
                contact
            }
        }

        // Mettre à jour le nom dans les appels
        appels = appels?.map { appel ->
            if (appel.entrepriseNom == oldName) {
                appel.copy(entrepriseNom = newName)
            } else {
                appel
            }
        }

        // Mettre à jour le nom dans les entretiens
        entretiens = entretiens?.map { entretien ->
            if (entretien.entrepriseNom == oldName) {
                entretien.copy(entrepriseNom = newName)
            } else {
                entretien
            }
        }

        // Mettre à jour le nom dans les relances
        relances = relances?.map { relance ->
            if (relance.entrepriseNom == oldName) {
                relance.copy(entrepriseNom = newName)
            } else {
                relance
            }
        }

        // Todo : Mettre à jour le nom dans les évènements

        // Sauvegarder les données mises à jour
        saveAllData()
    }

    private fun saveAllData() {
        sharedPreferences.edit().apply {
            putString("candidatures", gson.toJson(candidatures))
            putString("contacts", gson.toJson(contacts))
            putString("appels", gson.toJson(appels))
            putString("entretiens", gson.toJson(entretiens))
            putString("relances", gson.toJson(relances))
        }.apply()
    }

    fun updateCandidatureState(candidature: Candidature) {
        if (candidature.etatManuel) return

        val newState = determineState(candidature)


        if (candidature.state != newState) {
            candidature.state = newState
            saveCandidature(candidature)
            sendStateChangeNotification(candidature)
        }
    }

    private fun determineState(candidature: Candidature): CandidatureState {
        val today = Calendar.getInstance().time
        val daysSinceCandidated = (today.time - candidature.date_candidature.time) / (1000 * 60 * 60 * 24)
        val latestRelance = getLatestRelanceForCandidature(candidature.id)
        val entretiens = loadEntretiensForCandidature(candidature.id)
        val appels = loadAppelsForCandidature(candidature.id)

        val newState = when {
            entretiens.isNotEmpty() && candidature.retourPostEntretien -> CandidatureState.EN_ATTENTE_APRES_ENTRETIEN
            latestRelance != null && daysSinceCandidated <= 14 -> CandidatureState.RELANCEE_ET_EN_ATTENTE
            daysSinceCandidated <= 7 -> CandidatureState.CANDIDATEE_ET_EN_ATTENTE
            appels.isNotEmpty() && daysSinceCandidated > 14 -> CandidatureState.AUCUNE_REPONSE
            entretiens.isEmpty() && appels.isEmpty() && daysSinceCandidated > 7 -> CandidatureState.A_RELANCEE
            else -> CandidatureState.ERREUR
        }
        return newState
    }

    private fun sendStateChangeNotification(candidature: Candidature) {
        val message = "La candidature pour ${candidature.titre_offre} est ${candidature.state}"
        sendNotification(context, "État de candidature mis à jour", message)

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("CANDIDATURE_ID", candidature.id)
            putExtra("TITLE", "Candidature update")
            putExtra("MESSAGE", message)
        }
        context.sendBroadcast(intent)
    }

    fun getLatestRelanceForCandidature(candidature_id: String): Relance? {
        val allRelances = loadRelancesForCandidature(candidature_id)

        return allRelances
            .filter { it.candidatureId == candidature_id }
            .maxByOrNull { it.date_relance }
    }


    fun checkAndUpdateCandidatureStates() {
        val candidatures = getCandidatures()
        candidatures.forEach { updateCandidatureState(it) }
    }

    fun addCandidatureToContact(contactId: String, candidatureId: String) {
        val contact = getContactById(contactId)
        contact?.let {
            if (it.candidatureIds?.contains(candidatureId) == false) {
                it.candidatureIds?.add(candidatureId)
                saveContact(it)
            }
        }
    }

    fun generateGlobalData(dayOffset: Int = 0): String {
        val candidatures = loadCandidatures()
        val appels = getAppelsLast7Days()
        val relances = getRelancesLast7Days()
        val entretiens = getUpcomingInterviews(7) + getPastInterviews()

        val now = Calendar.getInstance()
        now.add(Calendar.DAY_OF_YEAR, dayOffset)
        val start = Calendar.getInstance()
        start.time = now.time
        start.add(Calendar.DAY_OF_YEAR, -6)

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val startDate = sdf.format(start.time)
        val endDate = sdf.format(now.time)

        val dailyCounts = mutableMapOf<String, Int>()
        val dailyAppels = mutableMapOf<String, Int>()
        val dailyRelances = mutableMapOf<String, Int>()
        val dailyEntretiens = mutableMapOf<String, Int>()

        for (i in 0..6) {
            val date = start.time
            val dateString = sdf.format(date)
            dailyCounts[dateString] = candidatures.count { sdf.format(it.date_candidature) == dateString }
            dailyAppels[dateString] = appels.count { sdf.format(it.date_appel) == dateString }
            dailyRelances[dateString] = relances.count { sdf.format(it.date_relance) == dateString }
            dailyEntretiens[dateString] = entretiens.count { sdf.format(it.date_entretien) == dateString }
            start.add(Calendar.DAY_OF_YEAR, 1)
        }

        val labels = JSONArray()
        val dataCandidatures = JSONArray()
        val dataAppels = JSONArray()
        val dataRelances = JSONArray()
        val dataEntretiens = JSONArray()
        for ((date) in dailyCounts) {
            labels.put(date)
            dataCandidatures.put(dailyCounts[date])
            dataAppels.put(dailyAppels[date])
            dataRelances.put(dailyRelances[date])
            dataEntretiens.put(dailyEntretiens[date])
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Candidatures")
                    put("backgroundColor", "#4CAF50")
                    put("data", dataCandidatures)
                })
                put(JSONObject().apply {
                    put("label", "Appels")
                    put("backgroundColor", "#FF6384")
                    put("data", dataAppels)
                })
                put(JSONObject().apply {
                    put("label", "Relances")
                    put("backgroundColor", "#36A2EB")
                    put("data", dataRelances)
                })
                put(JSONObject().apply {
                    put("label", "Entretiens")
                    put("backgroundColor", "#FFCE56")
                    put("data", dataEntretiens)
                })
            })
        }

        val options = JSONObject().apply {
            put("responsive", true)
            put("tooltips", JSONObject().apply {
                put("mode", "index")
                put("intersect", false)
            })
            put("scales", JSONObject().apply {
                put("yAxes", JSONArray().apply {
                    put(JSONObject().apply {
                        put("ticks", JSONObject().apply {
                            put("beginAtZero", true)
                            put("stepSize", 1)
                        })
                    })
                })
            })
        }

        val title = "Activité sur $startDate à $endDate"
        val graphId = "global_data_chart"
        return generate_graph(title, chartData, graphId, "bar", options)
    }

    private fun generate_graph(title: String, chartData: JSONObject, chartId: String, chartType: String, options: JSONObject): String {
        return """
    <html>
    <head>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    </head>
    <body>
        <h2>$title</h2>
        <canvas id="$chartId"></canvas>
        <script>
            var ctx = document.getElementById('$chartId').getContext('2d');
            var myChart = new Chart(ctx, {
                type: '$chartType',
                data: $chartData,
                options: $options
            });
        </script>
    </body>
    </html>
    """
    }
    fun createChartData(labels: JSONArray, data: JSONArray, label: String, colors: Array<String>): JSONObject {
        val backgroundColors = JSONArray(colors)  // Assume colors is an array of color strings.
        return JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", label)
                    put("backgroundColor", backgroundColors)
                    put("data", data)
                })
            })
        }
    }

    fun standardOptions(): JSONObject {
        return JSONObject().apply {
            put("responsive", true)
            put("scales", JSONObject().apply {
                put("yAxes", JSONArray().apply {
                    put(JSONObject().apply {
                        put("tricks", JSONObject().apply {
                            put("beginAtZero", true)
                            put("stepSize", 1)
                        })
                    })
                })
            })
        }
    }
    fun <T> getLast7DaysData(
        dayOffset: Int,
        loadData: () -> List<T>,
        dateExtractor: (T) -> Date,
        label: String,
        backgroundColor: String
    ): String {
        val uniformColor = "#4BC0C0"
        val now = Calendar.getInstance()
        now.add(Calendar.DAY_OF_YEAR, dayOffset)
        val end = Calendar.getInstance()
        end.time = now.time
        end.add(Calendar.DAY_OF_YEAR, -6)

        val dataItems = loadData()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val counts = mutableMapOf<String, Int>()

        for (date in 0..6) {
            val dateString = format.format(end.time)
            counts[dateString] = dataItems.count {
                format.format(dateExtractor(it)) == dateString
            }
            end.add(Calendar.DAY_OF_YEAR, 1)
        }

        val labels = JSONArray()
        val data = JSONArray()
        counts.forEach { (date, count) ->
            labels.put(date)
            data.put(count)
        }
        //val specificColors = arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF")

        val colors = Array(counts.size) { uniformColor }

        val chartData = createChartData(labels, data, label, colors)
        val options = standardOptions()

        val title = label
        val graphId = label.filter { it.isLetterOrDigit() }.toLowerCase() + "Chart"
        return generate_graph(title, chartData, graphId, "bar", options)
    }

    fun <T> getDataGroupedBy(
        loadData: () -> List<T>,
        groupByExtractor: (T) -> String,
        title: String,
        chartType: String = "pie",
        colors: Array<String>? = null,
        optionsCustomizer: (JSONObject) -> Unit = {}
    ): String {
        val dataItems = loadData()
        val groupedData = dataItems.groupBy(groupByExtractor).mapValues { it.value.size }

        val labels = JSONArray()
        val data = JSONArray()
        val backgroundColors = JSONArray()
        val specificColors = arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF") // Assurez-vous que la longueur de ce tableau couvre toutes vos catégories ou recyclez les couleurs

        groupedData.forEach { (key, value) ->
            labels.put(key)
            data.put(value)
            backgroundColors.put(specificColors[data.length() % specificColors.size])
        }

        val chartData = createChartData(labels, data, title, specificColors)
        val options = standardOptions()
        optionsCustomizer(options)

        val graphId = title.filter { it.isLetterOrDigit() }.toLowerCase() + "Chart"

        return generate_graph(title, chartData, graphId, chartType, options)
    }

    fun getCustomizedCandidaturesChart(): String {
        return getDataGroupedBy(
            ::loadCandidatures,
            { it.type_poste },
            "Customized Candidatures Chart",
            "bar",
            arrayOf("#FF6384"),
            { options ->
                options.getJSONObject("scales").getJSONArray("yAxes").getJSONObject(0).getJSONObject("ticks").put("stepSize", 5)
            }
        )
    }


    fun getDataPerPlatform(loadData: () -> List<Any>, labelExtractor: (Any) -> String, title: String): String {
        val dataItems = loadData()
        val counts = dataItems.groupBy(labelExtractor).mapValues { it.value.size }

        val labels = JSONArray()
        val data = JSONArray()
        val backgroundColors = JSONArray()
        val colors = arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F44", "#FF9740", "#FF9940", "#F89F40", "#FF9F40", "#FFAF40", "#FB9F40", "#FF9F40", "#FFDF40", "#FF9940", "#FF9FF0", "#FF9F40", "#2F9140", "#F69FB0", "#FF9A40")

        counts.forEach { (platform, count) ->
            labels.put(platform)
            data.put(count)
            backgroundColors.put(colors[data.length() % colors.size])
        }

        val chartData = createChartData(labels, data, title, colors)
        val options = standardOptions()

        val graphId = title.filter { it.isLetterOrDigit() }.toLowerCase() + "Chart"
        return generate_graph(title, chartData, graphId, "pie", options)
    }


    fun getCandidaturesPerLocation(): String {
        return getDataGroupedBy(
            ::loadCandidatures,
            { it.lieuPoste ?: "Unknown" },
            "Candidatures par lieu de poste",
            "pie",
            arrayOf("#36A2EB")
        )
    }

    fun getCandidaturesPerState(): String {
        return getDataGroupedBy(
            ::loadCandidatures,
            { it.state.toString() },
            "Candidatures par état",
            "pie",
            arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#F89F40", "#FFAF40") // Example color array
        )
    }

    fun getCandidaturesPerPlateforme(): String {
        return getDataGroupedBy(
            ::loadCandidatures,
            { it.plateforme },
            "Candidatures par plateforme",
            "pie",
            arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#F89F40", "#FFAF40")
        )
    }

    fun getCandidaturesPerTypePoste(): String {
        return getDataGroupedBy(
            ::loadCandidatures,
            { it.type_poste },
            "Candidatures par type de poste",
            "pie",
            arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#F89F40", "#FFAF40")
        )
    }

    fun getRelancesPerPlateforme(): String {
        return getDataGroupedBy(
            ::loadRelances,
            { it.plateformeUtilisee },
            "Relances par plateforme",
            "pie",
            arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#F89F40", "#FFAF40")
        )
    }

    fun getEntretiensPerTypeDatas(): String {
        return getDataGroupedBy(
            ::loadEntretiens,
            { it.type },
            "Entretiens par type",
            "pie",
            arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#F89F40", "#FFAF40")
        )
    }

    fun getEntretiensPerStyleDatas(): String {
        return getDataGroupedBy(
            ::loadEntretiens,
            { it.mode },
            "Entretiens par mode",
            "pie",
            arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#F89F40", "#FFAF40")
        )
    }

    fun getCandidaturesLast7Days(dayOffset: Int): String {
        val now = Calendar.getInstance()
        now.add(Calendar.DAY_OF_YEAR, dayOffset)
        val end = Calendar.getInstance()
        end.time = now.time
        end.add(Calendar.DAY_OF_YEAR, -6)

        val candidatures = loadCandidatures()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val counts = mutableMapOf<String, Int>()

        for (date in 0..6) {
            val dateString = format.format(end.time)
            counts[dateString] = candidatures.count {
                format.format(it.date_candidature) == dateString
            }
            end.add(Calendar.DAY_OF_YEAR, 1)
        }

        val labels = JSONArray()
        val data = JSONArray()
        counts.forEach { (date, count) ->
            labels.put(date)
            data.put(count)
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Candidatures des 7 derniers jours")
                    put("backgroundColor", "#4BC0C0")
                    put("data", data)
                    put("borderColor", "#007bff")
                    put("fill", false)
                })
            })
        }

        val title = "Candidatures des 7 derniers jours"
        val graphId = "candidaturesLast7DaysChart"
        val options = JSONObject().apply {
            put("responsive", true)
            put("tooltips", JSONObject().apply {
                put("mode", "index")
                put("intersect", false)
            })
            put("hover", JSONObject().apply {
                put("mode", "nearest")
                put("intersect", false)
            })
            put("scales", JSONObject().apply {
                put("yAxes", JSONObject().apply {
                    put("ticks", JSONObject().apply {
                        put("beginAtZero", true)
                        put("stepSize", 1)
                    })
                })
            })
        }

        return generate_graph(title, chartData, graphId, "line", options)
    }

    fun getEntretiensLast7Days(dayOffset: Int): String {
        val now = Calendar.getInstance()
        now.add(Calendar.DAY_OF_YEAR, dayOffset)
        val end = Calendar.getInstance()
        end.time = now.time
        end.add(Calendar.DAY_OF_YEAR, -6)

        val entretiens = loadEntretiens()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val counts = mutableMapOf<String, Int>()

        for (date in 0..6) {
            val dateString = format.format(end.time)
            counts[dateString] = entretiens.count {
                format.format(it.date_entretien) == dateString
            }
            end.add(Calendar.DAY_OF_YEAR, 1)
        }

        val labels = JSONArray()
        val data = JSONArray()
        counts.forEach { (date, count) ->
            labels.put(date)
            data.put(count)
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Entretiens des 7 derniers jours")
                    put("backgroundColor", "#4BC0C0")
                    put("data", data)
                })
            })
        }

        val options = JSONObject().apply {
            put("responsive", true)
            put("scales", JSONObject().apply {
                put("yAxes", JSONArray().apply {
                    put(JSONObject().apply {
                        put("ticks", JSONObject().apply {
                            put("beginAtZero", true)
                            put("stepSize", 1)  // Ensure the step size is one to avoid decimal values.
                        })
                    })
                })
            })
        }

        val title = "Entretiens des 7 derniers jours"
        val graphId = "entretiensLast7DaysChart"
        return generate_graph(title, chartData, graphId, "bar", options)
    }

    fun getAppelsLast7Days(): List<Appel> {
        val now = Calendar.getInstance()
        val start = Calendar.getInstance()
        start.add(Calendar.DAY_OF_YEAR, -7)

        return loadAppels().filter { it.date_appel in start.time..now.time }
    }

    fun getRelancesLast7Days(): List<Relance> {
        val now = Calendar.getInstance()
        val start = Calendar.getInstance()
        start.add(Calendar.DAY_OF_YEAR, -7)
        return loadRelances().filter { it.date_relance in start.time..now.time }
    }

    fun getAppelsLast7DaysDatas(dayOffset: Int): String {
        return getLast7DaysData(
            dayOffset,
            ::loadAppels,
            { it.date_appel },
            "Appels des 7 derniers jours",
            "#4EBE07"
        )
    }
    fun getRelancesLast7DaysDatas(dayOffset: Int): String {
        return getLast7DaysData(
            dayOffset,
            ::loadRelances,
            { it.date_relance },
            "Relances des 7 derniers jours",
            "#4BE0C0"
        )
    }


    fun getCandidaturesPerCompany(): String {
        val candidatures = loadCandidatures()
        val candidaturesCount = candidatures.groupBy { it.entrepriseNom }.mapValues { it.value.size }

        val labels = JSONArray()
        val data = JSONArray()
        val backgroundColors = JSONArray()
        val colors = arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF")

        candidaturesCount.forEach { (company, count) ->
            labels.put(company)
            data.put(count)
            backgroundColors.put(colors[data.length() % colors.size]) // Cycle through colors
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Candidatures par entreprise")
                    put("backgroundColor", backgroundColors)
                    put("data", data)
                })
            })
        }
        val options = JSONObject().apply {
            put("responsive", true)
            put("scales", JSONObject().apply {
                put("yAxes", JSONArray().apply {
                    put(JSONObject().apply {
                        put("ticks", JSONObject().apply {
                            put("beginAtZero", true)
                            put("stepSize", 1)
                        })
                    })
                })
            })
        }

        val title = "Candidatures par entreprise"
        val graphId = "candidaturesPerCompanyChart"
        return generate_graph(title, chartData, graphId, "bar", options)
    }
    fun getUpcomingInterviews(days: Int): List<Entretien> {
        val now = Calendar.getInstance()
        val future = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, days) }
        return loadEntretiens().filter { it.date_entretien in now.time..future.time }
    }

    fun getPastInterviews(): List<Entretien> {
        val now = Calendar.getInstance()
        return loadEntretiens().filter { it.date_entretien.before(now.time) }
    }

    fun getInterviewsPerCandidature(): String {
        val candidatures = loadCandidatures()
        val entretiens = loadEntretiens()

        val candidaturesCount = candidatures.size
        val entretiensCount = entretiens.groupBy { it.candidature_id }.mapValues { it.value.size }

        val labels = JSONArray()
        val data = JSONArray()
        for ((candidatureId, count) in entretiensCount) {
            val candidature = candidatures.find { it.id == candidatureId }
            labels.put(candidature?.titre_offre ?: "Unknown")
            data.put(count)
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Entretiens par candidature")
                    put("backgroundColor", "#FF9800")
                    put("data", data)
                })
            })
        }

        return """
            <html>
            <head>
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            </head>
            <body>
                <canvas id="myChart"></canvas>
                <script>
                    var ctx = document.getElementById('myChart').getContext('2d');
                    var myChart = new Chart(ctx, {
                        type: 'bar',
                        data: $chartData,
                        options: {
                            responsive: true,
                            scales: {
                                yAxes: [{
                                    ticks: {
                                        beginAtZero: true
                                    }
                                }]
                            }
                        }
                    });
                </script>
            </body>
            </html>
        """
    }


    fun sendNotification(context: Context, title: String, message: String) {
        Log.d("DataRepository", "sendNotification called with title: $title and message : $message")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("JOBBER_CHANNEL", "Jobber Notifications", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Channel for Jobber notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, "JOBBER_CHANNEL")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("NotificationReceiver", "Request permissions")
                return
            }
            Log.d("NotificationReceiver", "No request permissions because they're granted!")
            notify(1001, builder.build())
        }

        // Add the notification to the list
        val newNotification = Notification(
            titre = title,
            message = message,
            date = Date()
        )
        saveNotification(newNotification)
    }

    fun getNotifications(): List<Notification> {
        return notifications?.sortedByDescending { it.date } ?: emptyList()
    }
    fun deleteNotification(notification: Notification) {
        val mutableNotifications = notifications?.toMutableList() ?: mutableListOf()
        mutableNotifications.remove(notification)
        notifications = mutableNotifications
        saveNotifications()

        val intent = Intent("com.jobber.NOTIFICATION_LIST_UPDATED")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun markNotificationAsRead(notification: Notification) {
        deleteNotification(notification)
    }

    private fun saveNotifications() {
        val jsonString = gson.toJson(notifications)
        sharedPreferences.edit().putString("notifications", jsonString).apply()
    }

    private fun loadNotifications(): List<Notification> {
        val notificationsJson = sharedPreferences.getString("notifications", null)
        return if (notificationsJson != null) {
            val type = object : TypeToken<List<Notification>>() {}.type
            gson.fromJson(notificationsJson, type)
        } else {
            emptyList()
        }
    }

    private fun loadEvents(): List<Evenement> {
        val eventsJson = sharedPreferences.getString("evenements", null)
        return if (eventsJson != null) {
            val type = object : TypeToken<List<Evenement>>() {}.type
            gson.fromJson(eventsJson, type)
        } else {
            emptyList()
        }
    }

    fun saveNotification(notification: Notification) {
        val mutableNotifications = notifications?.toMutableList() ?: mutableListOf()
        mutableNotifications.add(notification)
        notifications = mutableNotifications
        val jsonString = gson.toJson(notifications)
        sharedPreferences.edit().putString("notifications", jsonString).apply()

        val intent = Intent("com.jobber.NOTIFICATION_LIST_UPDATED")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun saveEvent(evenement: Evenement) {
        val mutableEvents = evenements?.toMutableList() ?: mutableListOf()
        mutableEvents.add(evenement)
        evenements = mutableEvents
        val jsonString = gson.toJson(evenements)
        sharedPreferences.edit().putString("evenements", jsonString).apply()

        val intent = Intent("com.jobber.EVENTS_LIST_UPDATED")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    private fun saveEvents() {
        val jsonString = gson.toJson(evenements)
        sharedPreferences.edit().putString("evenements", jsonString).apply()
    }
    fun getEvents(): List<Evenement> {
        return evenements ?: emptyList()
    }

    fun getEventsOn(date: Date): List<Evenement> {
        val startOfDay = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val endOfDay = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        return evenements?.filter {
            it.startTime >= startOfDay && it.startTime <= endOfDay
        } ?: emptyList()
    }

    fun updateEvent(evenement: Evenement, appel: Appel, candidature: Candidature) {
        evenement.title = "Appel : ${appel.objet}"
        evenement.description =
            "Appel concernant ${candidature.titre_offre} chez ${candidature.entrepriseNom}"
        evenement.startTime = appel.date_appel.time
        evenement.endTime = appel.date_appel.time + 600000
        evenement.color = "#808080"
        saveEvent(evenement)
    }

    fun createEventForAppel(appel: Appel, candidature: Candidature) {
        val newEvenement = Evenement(
            id = UUID.randomUUID().toString(),
            title = "Appel: ${appel.objet}",
            description = "Call regarding ${candidature.titre_offre} at ${candidature.entrepriseNom}",
            startTime = appel.date_appel.time,
            endTime = appel.date_appel.time + 600000, // assuming 10 min duration
            type = EventType.Appel,
            relatedId = appel.id,
            entrepriseId = candidature.entrepriseNom,
            color = "#808080"
        )
        saveEvent(newEvenement)
    }
    fun createEventForRelance(relance: Relance, candidature: Candidature) {
        val newEvenement = Evenement(
            id = UUID.randomUUID().toString(),
            title = "Relance: ${candidature.titre_offre}",
            description = "Relance concernant ${candidature.titre_offre} at ${candidature.entrepriseNom}",
            startTime = relance.date_relance.time,
            endTime = relance.date_relance.time + 600000, // assuming 10 min duration
            type = EventType.Relance,
            relatedId = relance.id,
            entrepriseId = candidature.entrepriseNom,
            color = "#809970"
        )
        saveEvent(newEvenement)
    }

    fun createEventForEntretien(entretien: Entretien, candidature: Candidature) {
        val newEvenement = Evenement(
            id = UUID.randomUUID().toString(),
            title = "Entretien chez ${entretien.entrepriseNom} pour ${candidature.titre_offre} ",
            description = "Entretien concernant ${candidature.titre_offre} at ${candidature.entrepriseNom}",
            startTime = entretien.date_entretien.time,
            endTime = entretien.date_entretien.time + 600000, // assuming 10 min duration
            type = EventType.Entretien,
            relatedId = entretien.id,
            entrepriseId = candidature.entrepriseNom,
            color = "#8D9970"
        )
        saveEvent(newEvenement)
    }

    fun createEventForCandidature(candidature: Candidature) {
        val newEvenement = Evenement(
            id = UUID.randomUUID().toString(),
            title = "Candidature: ${candidature.titre_offre} pour ${candidature.entrepriseNom}",
            description = "Candidature concernant ${candidature.titre_offre} at ${candidature.entrepriseNom}",
            startTime = candidature.date_candidature.time,
            endTime = candidature.date_candidature.time + 600000, // assuming 10 min duration
            type = EventType.Candidature,
            relatedId = candidature.id,
            entrepriseId = candidature.entrepriseNom,
            color = "#809FF0"
        )
        saveEvent(newEvenement)
    }
    private fun generateDefaultEvents() {
        evenements = evenements ?: loadEvents()

        candidatures?.forEach { candidature ->
            if (evenements!!.none { it.relatedId == candidature.id && it.type == EventType.Candidature }) {
                createEventForCandidature(candidature)
            }
        }

        appels?.forEach { appel ->
            if (evenements!!.none { it.relatedId == appel.id && it.type == EventType.Appel }) {
                appel.candidature_id?.let { getCandidatureById(it) }?.let { createEventForAppel(appel, it) }
            }
        }

        relances?.forEach { relance ->
            if (evenements!!.none { it.relatedId == relance.id && it.type == EventType.Relance }) {
                relance.candidatureId?.let { getCandidatureById(it) }?.let { createEventForRelance(relance, it) }
            }
        }

        entretiens?.forEach { entretien ->
            if (evenements!!.none { it.relatedId == entretien.id && it.type == EventType.Entretien }) {
                entretien.candidature_id?.let { getCandidatureById(it) }?.let { createEventForEntretien(entretien, it) }
            }
        }
    }

    fun deleteEvent(evenement: Evenement) {
        val mutableEvents = evenements?.toMutableList() ?: mutableListOf()
        mutableEvents.remove(evenement)
        evenements = mutableEvents
        saveEvents()

        val intent = Intent("com.jobber.EVENTS_LIST_UPDATED")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun archiveCandidature(candidatureId: String) {
        val candidature = getCandidatureById(candidatureId)
        candidature?.let {
            it.archivee = true
            saveCandidature(it)
        }
    }

}