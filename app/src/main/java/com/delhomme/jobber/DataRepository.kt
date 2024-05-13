package com.delhomme.jobber

import android.content.Context
import android.util.Log
import com.delhomme.jobber.models.Candidature
import com.delhomme.jobber.models.Contact
import com.delhomme.jobber.models.Entreprise
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

    fun addContactToEntreprise(contact: Contact, entrepriseId: String) {
        val entreprise = getEntrepriseById(entrepriseId)
        if (entreprise != null) {
            if (entreprise.contacts == null) entreprise.contacts = mutableListOf()
            entreprise.contacts.add(contact)
            saveEntreprise(entreprise)
            Log.d("DataRepository","addContactToEntreprise : Liste des contacts de l'entreprise ${entreprise.nom} : ${entreprise.contacts}}")
            Log.d("DataRepository", "addContactToEntreprise : Updated entreprise with new contact: ${entreprise.contacts.size} contacts now")
        } else {
            Log.d("DataRepository", "addContactToEntreprise : No entreprise found with ID: $entrepriseId")
        }
    }
    fun getEntrepriseById(id: String): Entreprise? {
        Log.d("DataRepository", "id passée dans getEntrepriseById : $id")
        val entreprises = loadEntreprises()
        Log.d("DataRepository", "entreprises = loadEntreprises() : $entreprises")
        val entreprise = entreprises.find { it.id == id }
        Log.d("DataRepository", "Fetched entreprise : ${entreprise?.nom}")
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
            Log.d("DataRepository", "Entreprise sauvegardée: ${entreprise.nom} avec ${entreprise.contacts.size} contacts")
        }
    }

    fun reloadEntreprises() {
        val jsonString = sharedPreferences.getString("entreprises", null)
        if (jsonString != null) {
            val type = object : TypeToken<List<Entreprise>>() {}.type
            val entreprises = gson.fromJson<List<Entreprise>>(jsonString, type) ?: emptyList()
            entreprises.forEach { entreprise ->
                entreprise.contacts = loadContactsForEntreprise(entreprise.id)
                Log.d("DataRepository", "entreprise.contacts : ${entreprise.contacts}")
            }
        }
        Log.d("DataRepository", "Entreprise : $jsonString")
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
    // TODO ici je fait la récupération des contact théoriquement en filtrant par ID d'entreprise
    fun loadContactsForEntreprise(entrepriseId: String): MutableList<Contact> {
        val allContacts = loadContacts()
        Log.d("DataRepository", "allContacts : ${allContacts}")
        val filteredContacts2 = allContacts.filter { contact ->
            contact.entreprise?.id == entrepriseId
        }.toMutableList()
        Log.d("FilteredContact2", "Filtered2 ${filteredContacts2}")
        val filteredContacts = allContacts.filter { it.entreprise?.id == entrepriseId }.toMutableList()
        Log.d("DataRepository", "Filtered ${filteredContacts.size} contacts for entreprise ID: $entrepriseId")
        return filteredContacts
    }

    fun getOrCreateEntreprise(companyName: String): Entreprise {
        val existing = loadEntreprises().find { it.nom == companyName }
        if (existing != null) {
            Log.d("DataRepository", "Existing entreprise found: $existing")
            return existing
        }

        val newId = UUID.randomUUID().toString()

        val newEntreprise = Entreprise(id = newId, nom = companyName, contacts = mutableListOf())
        saveEntreprise(newEntreprise)
        Log.d("DataRepository", "getOrCreateEntreprise : Entreprise ajoutée et crée : $newEntreprise | ID : $newId")
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
            Log.d("DataRepository", "No contacts found in storage.")
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