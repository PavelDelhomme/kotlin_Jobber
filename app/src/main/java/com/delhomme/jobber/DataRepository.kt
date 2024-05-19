package com.delhomme.jobber

import android.content.Context
import android.util.Log
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.Entreprise.model.Entreprise
import com.delhomme.jobber.Entretien.model.Entretien
import com.delhomme.jobber.Relance.model.Relance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

    init {
        loadInitialData()
        checkAndUpdateCandidatureStates()
    }

    private fun loadInitialData() {
        candidatures = loadCandidatures()
        entreprises = loadEntreprises()
        contacts = loadContacts()
        appels = loadAppels()
        entretiens = loadEntretiens()
        relances = loadRelances()
    }

    fun getCandidatures() = candidatures ?: emptyList()
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
            }
        }
        candidatures = mutableCandidature
        val jsonString = gson.toJson(candidatures)
        sharedPreferences.edit().putString("candidatures", jsonString).apply()
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
                if (it.relanceIds == null) {
                    it.relanceIds = mutableListOf()
                }
                it.relanceIds.add(relance.id)
                saveEntreprise(it)
            }
            val candidature = getCandidatureById(relance.candidatureId)
            candidature?.let {
                if (it.relances == null) {
                    it.relances = mutableListOf()
                }
                it.relances.add(relance.id)
                saveCandidature(it)
            }
        }
        val jsonString = gson.toJson(relances)
        sharedPreferences.edit().putString("relances", jsonString).apply()
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
            }
        }

        val contact = getContactById(entretien.contact_id)
        contact?.let {
            if (it.candidatureIds?.contains(entretien.candidature_id) == false) {
                it.candidatureIds?.add(entretien.candidature_id)
                saveContact(it)
            }
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
        val jsonString = gson.toJson(appels)
        sharedPreferences.edit().putString("appels", jsonString).apply()
    }

    fun deleteCandidature(candidatureId: String) {
        val candidatures = loadCandidatures().toMutableList()
        val candidature = candidatures.find { it.id == candidatureId }
        candidatures.remove(candidature)
        val entreprise = getEntrepriseByNom(candidature?.entrepriseNom)
        entreprise?.candidatureIds?.remove(candidatureId)
        saveEntreprise(entreprise!!)
        val jsonString = gson.toJson(candidatures)
        sharedPreferences.edit().putString("candidatures", jsonString).apply()
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
        val jsonString = gson.toJson(filteredRelance)
        sharedPreferences.edit().putString("relances", jsonString).apply()
    }

    fun deleteEntretien(entretienId: String) {
        val currentEntretien = loadEntretiens().toMutableList()
        val filteredEntretien = currentEntretien.filter { it.id != entretienId }
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

    fun editCandidature(
        candidatureId: String,
        newTitre: String,
        newEtat: String,
        newNotes: String,
        newPlateforme: String,
        newTypePoste: String,
        newLieuPoste: String,
        newEntrepriseNom: String,
        newDate: Date,
        newEntretiens: MutableList<String>,
        newAppelsIds: MutableList<String>,
        newRelances: MutableList<String>
    ) {
        val candidatures = loadCandidatures().toMutableList()
        val index = candidatures.indexOfFirst { it.id == candidatureId }
        if (index != -1) {
            val oldCandidature = candidatures[index]
            val updatedCandidature = oldCandidature.copy(
                titre_offre = newTitre,
                etat = newEtat,
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
        }
    }


    fun editEntreprise(
        entrepriseNom: String,
        newName: String,
        newContactIds: MutableList<String>,
        newRelancesIds: MutableList<String>,
        newEntretiensIds: MutableList<String>,
        newCandidaturesIds: MutableList<String>
    ) {
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

    fun editRelance(
        relanceId: String,
        newDate: Date,
        newPlateformeUtilise: String,
        newEntrepriseNom: String,
        newContactId: String?,
        newCandidatureId: String,
        newNotes: String?
    ) {
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
    fun editAppel(
        appelId: String,
        newCandidatureId: String,
        newContactId: String?,
        newEntrepriseNom: String,
        newDateAppel: Date,
        newObjet: String,
        newNotes: String
    ) {
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

    fun editContact(
        contactId: String,
        newNom: String,
        newPrenom: String,
        newEmail: String,
        newTelephone: String,
        newEntrepriseNom: String,
        newAppelsIds: MutableList<String>,
        newCandidatureIds: MutableList<String>
    ) {
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

    fun editEntretien(
        entretienId: String,
        newEntrepriseNom: String,
        newContactId: String?,
        newCandidatureId: String,
        newDateEntretien: Date,
        newType: String,
        newMode: String,
        newNotesPreEntretien: String?,
        newNotesPostEntretien: String?
    ) {
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
        val today = Calendar.getInstance().time
        val candidatedDate = candidature.date_candidature
        val daysSinceCandidated = (today.time - candidatedDate.time) / (1000 * 60 * 60 * 24)

        // Check si aucun entretiens, appels, ou relances associée avec cette candidature
        val entretiens = loadEntretiensForCandidature(candidature.id)
        val appels = loadAppelsForCandidature(candidature.id)
        val relances = loadRelancesForCandidature(candidature.id)

        val sevenDaysAfterCandidature = Date(candidatedDate.time + 7 * (1000 * 60 * 60 * 24))

        val newState = when {
            // Candidature est dans les 7 jours suivant la candidature
            daysSinceCandidated <= 7 -> CandidatureState.CANDIDATEE_ET_EN_ATTENTE

            // Aucun entretien, appel, interaction ou relance dans les 7 derniers jours suivant la candidature
            entretiens.isEmpty() && appels.isEmpty() && relances.none { it.date_relance <= sevenDaysAfterCandidature } -> CandidatureState.A_RELANCEE

            // Relance ajoutée avant ou après les 7 jours suivant la candidature
            relances.any { it.date_relance > sevenDaysAfterCandidature } -> CandidatureState.RELANCEE_ET_EN_ATTENTE

            // En attente après entretien si retour post-entretien effectué
            entretiens.isNotEmpty() && candidature.retourPostEntretien && daysSinceCandidated <= 14 -> CandidatureState.EN_ATTENTE_APRES_ENTRETIEN

            // Aucune réponse après relance
            relances.isNotEmpty() && daysSinceCandidated > 14 -> CandidatureState.AUCUNE_REPONSE

            // Par défaut
            else -> CandidatureState.ERREUR
        }

        candidature.state = newState
        saveCandidature(candidature)
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


    fun getUpcomingInterviews(days: Int): List<Entretien> {
        val now = Calendar.getInstance()
        val future = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, days) }
        return loadEntretiens().filter { it.date_entretien in now.time..future.time }
    }

    fun getGraphData(dayOffset: Int = 0): String {
        val candidatures = loadCandidatures()
        val now = Calendar.getInstance()
        now.add(Calendar.DAY_OF_YEAR, dayOffset)
        val start = Calendar.getInstance()
        start.time = now.time
        start.add(Calendar.DAY_OF_YEAR, -6)

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val startDate = sdf.format(start.time)
        val endDate = sdf.format(now.time)

        val dailyCounts = mutableMapOf<String, Int>()
        for (i in 0..6) {
            val date = start.time
            val dateString = sdf.format(date)
            dailyCounts[dateString] = candidatures.count { sdf.format(it.date_candidature) == dateString }
            start.add(Calendar.DAY_OF_YEAR, 1)
        }

        val labels = JSONArray()
        val data = JSONArray()
        for ((date, count) in dailyCounts) {
            labels.put(date)
            data.put(count)
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Candidatures par jour")
                    put("backgroundColor", "#4CAF50")
                    put("data", data)
                })
            })
        }

        val title = "Candidatures sur $startDate à $endDate"

        val htmlContent = """
        <html>
        <head>
            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        </head>
        <body>
            <h2>$title</h2>
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
        Log.d("DataRepository", "HTML content: $htmlContent")
        return htmlContent
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

    fun getCandidaturesPerCompany(): String {
        val candidatures = loadCandidatures()
        val candidaturesCount =
            candidatures.groupBy { it.entrepriseNom }.mapValues { it.value.size }

        val labels = JSONArray()
        val data = JSONArray()
        for ((company, count) in candidaturesCount) {
            labels.put(company)
            data.put(count)
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Candidatures par entreprise")
                    put("backgroundColor", "#FF6384")
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
                    type: 'pie',
                    data: $chartData,
                    options: {
                        responsive: true,
                    }
                });
            </script>
        </body>
        </html>
    """
    }

    fun getCandidaturesPerLocation(): String {
        val candidatures = loadCandidatures()
        val candidaturesCount = candidatures.groupBy { it.lieuPoste }.mapValues { it.value.size }

        val labels = JSONArray()
        val data = JSONArray()
        for ((location, count) in candidaturesCount) {
            labels.put(location ?: "Unknown")
            data.put(count)
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Candidatures par lieu de poste")
                    put("backgroundColor", "#36A2EB")
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
                    type: 'pie',
                    data: $chartData,
                    options: {
                        responsive: true,
                    }
                });
            </script>
        </body>
        </html>
    """
    }

    fun getCandidaturesPerState(): String {
        val candidatures = loadCandidatures()
        val candidaturesCount = candidatures.groupBy { it.etat }.mapValues { it.value.size }

        val labels = JSONArray()
        val data = JSONArray()
        for ((state, count) in candidaturesCount) {
            labels.put(state)
            data.put(count)
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Candidatures par état")
                    put("backgroundColor", "#FFCE56")
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
                    type: 'pie',
                    data: $chartData,
                    options: {
                        responsive: true,
                    }
                });
            </script>
        </body>
        </html>
    """
    }

    fun getCandidaturesPerPlateforme(): String {
        val candidatures = loadCandidatures()
        val candidaturesCount = candidatures.groupBy { it.plateforme }.mapValues { it.value.size }

        val labels = JSONArray()
        val data = JSONArray()
        for ((plateforme, count) in candidaturesCount) {
            labels.put(plateforme)
            data.put(count)
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Candidatures par plateforme")
                    put("backgroundColor", "#4BC0C0")
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
                    type: 'pie',
                    data: $chartData,
                    options: {
                        responsive: true,
                    }
                });
            </script>
        </body>
        </html>
    """
    }

    fun getCandidaturesPerTypePoste(): String {
        val candidatures = loadCandidatures()
        val candidaturesCount = candidatures.groupBy { it.type_poste }.mapValues { it.value.size }

        val labels = JSONArray()
        val data = JSONArray()
        for ((typePoste, count) in candidaturesCount) {
            labels.put(typePoste)
            data.put(count)
        }

        val chartData = JSONObject().apply {
            put("labels", labels)
            put("datasets", JSONArray().apply {
                put(JSONObject().apply {
                    put("label", "Candidatures par type de poste")
                    put("backgroundColor", "#9966FF")
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
                    type: 'pie',
                    data: $chartData,
                    options: {
                        responsive: true,
                    }
                });
            </script>
        </body>
        </html>
    """
    }
}