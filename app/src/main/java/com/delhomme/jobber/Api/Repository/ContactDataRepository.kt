package com.delhomme.jobber.Api.Repository

import android.content.Context
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
}