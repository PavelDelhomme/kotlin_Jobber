package com.delhomme.jobber

import android.content.Context
import android.util.Log
import com.delhomme.jobber.models.Appel
import com.delhomme.jobber.models.Candidature
import com.delhomme.jobber.models.Contact
import com.delhomme.jobber.models.Entreprise
import com.delhomme.jobber.models.Entretien
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class DataRepository(val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("CandidaturesPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCandidature(candidature: Candidature) {
        val candidatures = loadCandidatures().toMutableList()
        candidatures.add(candidature)
        val jsonString = gson.toJson(candidatures)
        sharedPreferences.edit().putString("candidatures", jsonString).apply()
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

    fun deleteCandidature(candidatureId: String) {
        val currentCandidature = loadCandidatures().toMutableList()
        val filteredCandidature = currentCandidature.filter { it.id != candidatureId }
        val jsonString = gson.toJson(filteredCandidature)
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

    fun deleteEntretien(entretienId: String) {
        val currentEntretien = loadEntretiens().toMutableList()
        val filteredEntretien = currentEntretien.filter { it.id != entretienId}
        val jsonString = gson.toJson(filteredEntretien)
        sharedPreferences.edit().putString("entretiens", jsonString).apply()
    }

    fun addContactToEntreprise(contact: Contact, entrepriseId: String) {
        val entreprise = getEntrepriseById(entrepriseId)
        if (entreprise != null) {
            if (entreprise.contacts == null) entreprise.contacts = mutableListOf()
            entreprise.contacts.add(contact)
            saveEntreprise(entreprise)
        } else {
            Log.d("DataRepository", "addContactToEntreprise : No entreprise found with ID: $entrepriseId")
        }
    }
    fun getEntrepriseById(id: String): Entreprise? {
        val entreprises = loadEntreprises()
        val entreprise = entreprises.find { it.id == id }
        return entreprise
        //val entrepriseString = sharedPreferences.getString("entreprises", "")
        //Log.d("DataRepository", "entrepriseString récupérer dans sharedPreferences : $entrepriseString")
        //val type = object : TypeToken<List<Entreprise>>() {}.type
        //val entreprises = gson.fromJson<List<Entreprise>>(entrepriseString, type)
        //Log.d("DataRepository", "entreprises récupérer du gson.fromJson : ${gson.fromJson<List<Entreprise>>(entrepriseString, type)}")
        //return entreprises.find { it.id == id }
    }

    fun getCandidatureById(id: String): Candidature? {
        val candidatureString = sharedPreferences.getString("candidatures", "")
        val type = object : TypeToken<List<Candidature>>() {}.type
        val candidatures = gson.fromJson<List<Candidature>>(candidatureString, type) ?: return null
        return candidatures.find { it.id == id }
    }

    fun getEntretienById(id: String): Entretien? {
        val entretienString = sharedPreferences.getString("entretiens", "")
        val type = object : TypeToken<List<Entretien>>() {}.type
        val entretiens = gson.fromJson<List<Entretien>>(entretienString, type) ?: return null
        return entretiens.find { it.id == id }
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

    fun saveEntretien(entretien: Entretien?) {
        if (entretien != null) {
            val entretiens = loadEntretiens().toMutableList()
            val index = entretiens.indexOfFirst { it.id == entretien.id }
            if (index != -1) {
                entretiens[index] = entretien
            } else {
                entretiens.add(entretien)
            }
            val jsonString = gson.toJson(entretiens)
            sharedPreferences.edit().putString("entretiens", jsonString).apply()
        }
    }

    fun reloadEntreprises() {
        val jsonString = sharedPreferences.getString("entreprises", null)
        if (jsonString != null) {
            val type = object : TypeToken<List<Entreprise>>() {}.type
            val entreprises = gson.fromJson<List<Entreprise>>(jsonString, type) ?: emptyList()
            entreprises.forEach { entreprise ->
                entreprise.contacts = loadContactsForEntreprise(entreprise.id)
            }
        }
    }

    fun reloadEntretiens() {
        val jsonString = sharedPreferences.getString("entretiens", null)
        if (jsonString != null) {
            val type = object : TypeToken<List<Entretien>>() {}.type
            val entretiens = gson.fromJson<List<Entretien>>(jsonString, type) ?: emptyList()
        }
    }
    fun loadEntreprises(): List<Entreprise> {
        reloadEntreprises()
        val jsonString = sharedPreferences.getString("entreprises", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<Entreprise>>() {}.type
            val entreprises = gson.fromJson<List<Entreprise>>(jsonString, type) ?: emptyList()
            entreprises.forEach { entreprise ->
                entreprise.contacts = loadContactsForEntreprise(entreprise.id)
            }
            entreprises
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

    fun saveAppel(appel: Appel) {
        val appels = loadAppels().toMutableList()
        appels.add(appel)
        val jsonString = gson.toJson(appels)
        sharedPreferences.edit().putString("appels", jsonString).apply()
    }

    fun getAppelById(id: String): Appel? {
        val appelString = sharedPreferences.getString("appels", "")
        val type = object : TypeToken<List<Appel>>() {}.type
        val appels = gson.fromJson<List<Appel>>(appelString, type) ?: return null
        return appels.find { it.id == id }
    }

    fun loadAppelsForContact(contact_id: String): List<Appel> {
        return loadAppels().filter { it.contact_id == contact_id }
    }

    fun loadAppelsForEntreprise(entreprise_id: String): List<Appel> {
        return loadAppels().filter { it.entreprise_id == entreprise_id }
    }

    fun loadAppelsForCandidature(entreprise_id: String): List<Appel> {
        return loadAppels().filter { it.entreprise_id == entreprise_id }
    }

    fun loadEntretienForEntreprise(entreprise_id: String): List<Entretien> {
        return loadEntretiens().filter { it.entreprise_id == entreprise_id }
    }

    fun loadEntretiensForCandidature(candidatureId: String): List<Entretien> {
        return loadEntretiens().filter { it.candidature_id == candidatureId }
    }
    // TODO FIXED ici je fait la récupération des contact théoriquement en filtrant par ID d'entreprise
    fun loadContactsForEntreprise(entrepriseId: String): MutableList<Contact> {
        val allContacts = loadContacts()
        val filteredContacts = allContacts.filter { it.entreprise?.id == entrepriseId }.toMutableList()
        return filteredContacts
    }

    fun getOrCreateEntreprise(companyName: String): Entreprise {
        val existing = loadEntreprises().find { it.nom == companyName }
        if (existing != null) {
            if (existing.entretiens == null) {
                existing.entretiens = mutableListOf()
            }
            return existing
        }

        val newId = UUID.randomUUID().toString()

        val newEntreprise = Entreprise(id = newId, nom = companyName, contacts = mutableListOf(), entretiens = mutableListOf())
        saveEntreprise(newEntreprise)
        return newEntreprise
    }


    fun loadContacts(): List<Contact> {
        val contactsJson = sharedPreferences.getString("contacts", null)
        if (contactsJson != null) {
            val type = object : TypeToken<List<Contact>>() {}.type
            val contacts = gson.fromJson<List<Contact>>(contactsJson, type)
            contacts.forEach { contact ->
                Log.d("DataRepository", "Loaded contact ${contact.id} with entreprise ID : ${contact.entreprise?.id}")
            }
            return contacts
        } else {
            return emptyList()
        }
    }

    fun getContactById(id: String): Contact? {
        val contactString = sharedPreferences.getString("contacts", "")
        val type = object : TypeToken<List<Contact>>() {}.type
        val contacts = gson.fromJson<List<Contact>>(contactString, type) ?: return null
        return contacts.find { it.id == id }
    }
    fun saveContact(contact: Contact) {
        val contacts = loadContacts().toMutableList()
        contacts.add(contact)
        val jsonString = gson.toJson(contacts)
        sharedPreferences.edit().putString("contacts", jsonString).apply()
    }

    fun updateContact(contact: Contact) {
        val contacts = loadContacts().toMutableList()
        val index = contacts.indexOfFirst { it.id == contact.id }
        if (index != -1) {
            contacts[index] = contact
            val jsonString = gson.toJson(contacts)
            sharedPreferences.edit().putString("contacts", jsonString).apply()
        }
    }

}