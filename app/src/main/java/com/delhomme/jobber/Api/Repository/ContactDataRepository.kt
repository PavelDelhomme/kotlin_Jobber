package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Model.Contact

class ContactDataRepository(context: Context) : BaseDataRepository<Contact>(context, "contacts") {

    override fun updateOrAddItem(mutableItems: MutableList<Contact>, item: Contact) {
        val mutableItems = items ?: mutableListOf()
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
        items = mutableItems
        saveItemsToPrefs(items!!)
    }


    fun loadContactsForEntreprise(entrepriseNom: String): List<Contact> {
        return findByCondition { it.entrepriseNom == entrepriseNom }
    }

    fun loadContactsForCandidature(candidatureId: String): List<Contact> {
        return findByCondition { it.candidatureIds!!.contains(candidatureId) }
    }
    fun getOrCreateContact(nom: String, prenom: String, entrepriseNom: String): Contact {
        val contact = items?.find { it.nom == nom && it.prenom == prenom && it.entrepriseNom == entrepriseNom }
        return contact ?: Contact(nom = nom, prenom = prenom, email = "", telephone = "", entrepriseNom = entrepriseNom).also {
            updateOrAddItem(items ?: mutableListOf(), it)
        }
    }

    fun addOrUpdateContact(contact: Contact) {
        val index = items?.indexOfFirst { it.id == contact.id }
        if (index != null && index != -1) {
            items!![index] = contact
        } else {
            items?.add(contact)
        }
        saveItemsToPrefs(items!!)
    }

    fun deleteContact(contactId: String) {
        items?.let { contacts ->
            val contactToRemove = contacts.firstOrNull { it.id == contactId }
            contactToRemove?.let {
                val appelDataRepository = AppelDataRepository(context)
                appelDataRepository.deleteAppelByContactId(contactId)
                contacts.remove(it)
                saveItemsToPrefs(contacts)
            }
        }
    }


}