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
import java.util.UUID

class DataRepository(val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("CandidaturesPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCandidature(candidature: Candidature) {
        val candidatures = loadCandidatures().toMutableList()
        val index = candidatures.indexOfFirst { it.id == candidature.id }
        if (index != -1) {
            candidatures[index] = candidature // Mise à jour
        } else {
            candidatures.add(candidature) // Ajout
            val entreprise = getEntrepriseById(candidature.entrepriseId)
            entreprise?.candidatureIds?.add(candidature.id)
            saveEntreprise(entreprise)
        }
        val jsonString = gson.toJson(candidatures)
        sharedPreferences.edit().putString("candidatures", jsonString).apply()
    }

    fun saveEntreprise(entreprise: Entreprise?) {
        if (entreprise != null) {
            val entreprises = loadEntreprises().toMutableList()
            val index = entreprises.indexOfFirst { it.id == entreprise.id }
            if (index != -1) {
                entreprises[index] = entreprise
            } else {
                entreprises.add(entreprise)
            }
            val jsonString = gson.toJson(entreprises)
            sharedPreferences.edit().putString("entreprises", jsonString).apply()
        }
    }

    fun saveRelance(relance: Relance) {
        val relances = loadRelances().toMutableList()
        val index = relances.indexOfFirst { it.id == relance.id }
        if (index != -1) {
            relances[index] = relance
        } else {
            relances.add(relance)
            val entreprise = getEntrepriseById(relance.entrepriseId)
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
        val entreprise = getEntrepriseById(candidature?.entrepriseId)
        entreprise?.candidatureIds?.remove(candidatureId)
        saveEntreprise(entreprise)
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

    fun deleteEntreprise(entrepriseId: String) {
        val currentEntreprise = loadEntreprises().toMutableList()
        val filteredEntreprise = currentEntreprise.filter { it.id != entrepriseId }
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
        val filteredEntretien = currentEntretien.filter { it.id != entretienId}
        val jsonString = gson.toJson(filteredEntretien)
        sharedPreferences.edit().putString("entretiens", jsonString).apply()
    }

    fun addContactToEntreprise(contactId: String, entrepriseId: String) {
        val entreprise = getEntrepriseById(entrepriseId)
        if (entreprise != null) {
            if (!entreprise.contactIds.contains(contactId)) {
                entreprise.contactIds.add(contactId)
                saveEntreprise(entreprise)
            }
        } else {
            Log.d("DataRepository", "addContactToEntreprise : No entreprise found with ID: $entrepriseId")
        }
    }
    fun getEntrepriseById(id: String?): Entreprise? {
        val entreprises = loadEntreprises()
        return entreprises.find { it.id == id }
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


    fun loadRelances(): List<Relance> {
        val jsonString = sharedPreferences.getString("relances", null)
        return if (!jsonString.isNullOrEmpty()) {
            val type = object : TypeToken<List<Relance>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }
    fun loadCandidatures(): List<Candidature>{
        val jsonString = sharedPreferences.getString("candidatures", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Candidature>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }


    fun loadEntreprises(): List<Entreprise> {
        val jsonString = sharedPreferences.getString("entreprises", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Entreprise>>() {}.type
            gson.fromJson<List<Entreprise>>(jsonString, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun loadAppels(): List<Appel> {
        val appelsJson = sharedPreferences.getString("appels", null)
        return if (appelsJson != null) {
            val type = object : TypeToken<List<Appel>>() {}.type
            gson.fromJson(appelsJson, type)
        } else {
            emptyList()
        }
    }
    fun loadEntretiens(): List<Entretien> {
        val entretiensJson = sharedPreferences.getString("entretiens", null)
        return if (entretiensJson != null) {
            val type = object : TypeToken<List<Entretien>>() {}.type
            gson.fromJson(entretiensJson, type)
        } else {
            emptyList()
        }
    }

    fun loadContacts(): List<Contact> {
        val contactsJson = sharedPreferences.getString("contacts", null)
        if (contactsJson != null) {
            val type = object : TypeToken<List<Contact>>() {}.type
            val contacts = gson.fromJson<List<Contact>>(contactsJson, type)
            contacts.forEach { contact ->
                Log.d("DataRepository", "Loaded contact ${contact.id} with entreprise ID : ${contact.entrepriseId}")
            }
            return contacts
        } else {
            return emptyList()
        }
    }


    fun loadAppelsForContact(contact_id: String): List<Appel> {
        return loadAppels().filter { it.contact_id == contact_id }
    }

    fun loadAppelsForEntreprise(entreprise_id: String): List<Appel> {
        return loadAppels().filter { it.entreprise_id == entreprise_id }
    }

    fun loadAppelsForCandidature(candidature_id: String): List<Appel> {
        return loadAppels().filter { it.candidature_id == candidature_id }
    }

    fun loadCandidaturesForEntreprise(entreprise_id: String): List<Candidature> {
        return loadCandidatures().filter { it.entrepriseId == entreprise_id }
    }

    fun loadEntretiensForEntreprise(entreprise_id: String): List<Entretien> {
        return loadEntretiens().filter { it.entreprise_id == entreprise_id }
    }

    fun loadEntretiensForCandidature(candidatureId: String): List<Entretien> {
        return loadEntretiens().filter { it.candidature_id == candidatureId }
    }
    // TODO FIXED ici je fait la récupération des contact théoriquement en filtrant par ID d'entreprise
    fun loadContactsForEntreprise(entrepriseId: String?): List<Contact> {
        return loadContacts().filter { it.entrepriseId == entrepriseId }
    }

    fun loadRelancesForEntreprise(entrepriseId: String?): List<Relance> {
        return loadRelances().filter { it.entrepriseId == entrepriseId }
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

        val newId = UUID.randomUUID().toString()
        val newEntreprise = Entreprise(id = newId, nom = companyName, contactIds = mutableListOf(), entretiens = mutableListOf())
        saveEntreprise(newEntreprise)
        return newEntreprise
    }

}