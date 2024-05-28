package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Model.Contact

class ContactDataRepository(context: Context) : BaseDataRepository<Contact>(context, "contacts") {

    override fun updateOrAddItem(mutableItems: MutableList<Contact>, item: Contact) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
    }
    fun loadContactsForEntreprise(entrepriseNom: String): List<Contact> {
        return findByCondition { it.entrepriseNom == entrepriseNom }
    }

    fun loadContactsForCandidature(candidatureId: String): List<Contact> {
        return findByCondition { it.candidatureIds!!.contains(candidatureId) }
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