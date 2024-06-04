package com.delhomme.jobber.Api.Repository

import android.content.Context
import android.util.Log
import com.delhomme.jobber.Model.Entreprise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class EntrepriseDataRepository(context: Context) : BaseDataRepository<Entreprise>(context, "entreprises") {

    private val listType: Type = object : TypeToken<List<Entreprise>>() {}.type
    private val gson = Gson()

    override fun updateOrAddItem(mutableItems: MutableList<Entreprise>, item: Entreprise) {
        val index = mutableItems.indexOfFirst { it.nom == item.nom }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
    }

    fun addOrUpdateEntreprise(entreprise: Entreprise) {

    }

    fun loadEntreprises(): List<Entreprise> {
        return allItems?: listOf()
    }
    fun getOrCreateEntreprise(nom: String): Entreprise {
        return allItems?.find { it.nom == nom } ?: Entreprise(nom).also {
            updateOrAddItem(allItems ?: mutableListOf(), it)
        }
    }
    fun reloadEntreprises() {
        allItems = loadItems().toMutableList()
    }

    fun convertJsonToItems(jsonString: String): List<Entreprise> {
        return try {
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            Log.e("EntrepriseDataRepository", "Error parsing JSON", e)
            mutableListOf()
        }
    }
}
