package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Model.Entreprise

class EntrepriseDataRepository(context: Context) : BaseDataRepository<Entreprise>(context, "entreprises") {

    override fun updateOrAddItem(mutableItems: MutableList<Entreprise>, item: Entreprise) {
        val index = mutableItems.indexOfFirst { it.nom == item.nom }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
    }
}
