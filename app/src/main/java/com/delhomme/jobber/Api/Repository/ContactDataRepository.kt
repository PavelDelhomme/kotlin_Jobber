package com.delhomme.jobber.Api.Repository

import android.content.Context
import android.util.Log
import com.delhomme.jobber.Contact.model.Contact
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ContactDataRepository(context: Context) : BaseDataRepository<Contact>(context, "contacts") {

    private val listType: Type = object : TypeToken<List<Contact>>() {}.type
    private val gson = Gson()


    override fun updateOrAddItem(mutableItems: MutableList<Contact>, item: Contact) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
        saveItemsToPrefs(mutableItems)
    }

    fun convertJsonToItems(jsonString: String): MutableList<Contact> {
        return try {
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            Log.e("ContactDataRepository", "Error parsing JSON", e)
            mutableListOf()
        }
    }

    fun loadContactsForEntreprise(entrepriseNom: String): List<Contact> {
        return findByCondition { it.entreprise == entrepriseNom }
    }

    fun loadContactsForCandidature(candidatureId: String): List<Contact> {
        return findByCondition { it.candidatureIds!!.contains(candidatureId) }
    }
    fun getOrCreateContact(nom: String, prenom: String, entrepriseNom: String): Contact {
        val contact = allItems?.find { it.nom == nom && it.prenom == prenom && it.entreprise == entrepriseNom }
        return contact ?: Contact(nom = nom, prenom = prenom, email = "", telephone = "", entreprise = entrepriseNom).also {
            updateOrAddItem(allItems ?: mutableListOf(), it)
        }
    }

    fun addOrUpdateContact(contact: Contact) {
        val index = allItems?.indexOfFirst { it.id == contact.id }
        if (index != null && index != -1) {
            allItems!![index] = contact
        } else {
            allItems?.add(contact)
        }
        saveItemsToPrefs(allItems!!)
    }

    fun deleteContact(contactId: String) {
        allItems?.let { contacts ->
            val contactToRemove = contacts.firstOrNull { it.id == contactId }
            contactToRemove?.let {
                // Suppression des appels liés
                val appelDataRepository = AppelDataRepository(context)
                appelDataRepository.deleteAppelsByContactId(contactId)

                // Suppression des événements liés (si applicable)
                val eventRepository = EvenementDataRepository(context)
                eventRepository.deleteEventsByContactId(contactId)

                // Supprimer le contact de la liste
                contacts.remove(it)
                saveItemsToPrefs(contacts)
            }
        }
    }


}