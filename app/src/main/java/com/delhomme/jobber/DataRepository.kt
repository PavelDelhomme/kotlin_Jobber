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

    fun addContactToEntreprise(contact: Contact, entrepriseId: String) {
        val entreprise = getEntrepriseById(entrepriseId)
        if (entreprise != null) {
            if (entreprise.contacts == null) entreprise.contacts = mutableListOf()
            entreprise.contacts.add(contact)
            saveEntreprise(entreprise)
            Log.d("addContactToEntreprise","Liste des contacts de l'entreprise ${entreprise.nom} : ${entreprise.contacts}}")
            Log.d("addContactToEntreprise", "Updated entreprise with new contact: ${entreprise.contacts.size} contacts now")
        } else {
            Log.d("addContactToEntreprise", "No entreprise found with ID: $entrepriseId")
        }
    }
    fun getEntrepriseById(id: String): Entreprise? {
        val entrepriseString = sharedPreferences.getString("entreprises", "")
        val type = object : TypeToken<List<Entreprise>>() {}.type
        val entreprises = gson.fromJson<List<Entreprise>>(entrepriseString, type) ?: return null
        return entreprises.find { it.id == id }
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
            }
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

    fun loadContactsForEntreprise(entrepriseId: String): MutableList<Contact> {
        return loadContacts().filter { it.entreprise_id == entrepriseId }.toMutableList()
    }

    fun getOrCreateEntreprise(companyName: String): Entreprise {
        val existing = loadEntreprises().find { it.nom == companyName }
        if (existing != null) return existing

        val newEntreprise = Entreprise(id = UUID.randomUUID().toString(), nom = companyName, contacts = mutableListOf())
        saveEntreprise(newEntreprise)
        Log.d("getOrCreateEntreprise", "Entreprise ajoutée et crée : $newEntreprise")
        return newEntreprise
    }



    fun loadContacts(): List<Contact> {
        val contactsJson = sharedPreferences.getString("contacts", null)
        return if (contactsJson != null) {
            val type = object : TypeToken<List<Contact>>() {}.type
            gson.fromJson(contactsJson, type)
        } else {
            emptyList()
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

    // Méthode pour supprimer un contact, si nécessaire
    fun deleteContact(contactId: String) {
        val contacts = loadContacts().toMutableList()
        contacts.removeAll { it.id == contactId }
        val jsonString = gson.toJson(contacts)
        sharedPreferences.edit().putString("contacts", jsonString).apply()
    }
}