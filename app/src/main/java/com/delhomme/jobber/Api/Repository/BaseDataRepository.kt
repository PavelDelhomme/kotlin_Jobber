package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

abstract class BaseDataRepository<T>(val context: Context, private val sharedPrefsKey: String) {
    protected var items: MutableList<T>? = null
    private val gson = Gson()

    init {
        items = loadItems().toMutableList()
    }

    fun getItems(): List<T> {
        return items ?: emptyList()
    }

    fun saveItem(item: T) {
        val mutableItems = items ?: mutableListOf()
        updateOrAddItem(mutableItems, item)
        items = mutableItems
        saveItemsToPrefs(items!!)
    }

    private fun loadItems(): List<T> {
        val jsonString = context.getSharedPreferences("JobberPrefs", Context.MODE_PRIVATE).getString(sharedPrefsKey, null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    protected fun saveItemsToPrefs(items: List<T>) {
        val jsonString = gson.toJson(items)
        context.getSharedPreferences("JobberPrefs", Context.MODE_PRIVATE).edit().putString(sharedPrefsKey, jsonString).apply()
    }

    abstract fun updateOrAddItem(mutableItems: MutableList<T>, item: T)
}
