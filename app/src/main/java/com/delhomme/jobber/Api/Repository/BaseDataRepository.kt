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

    fun loadItems(): List<T> {
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
    fun findByCondition(predicate: (T) -> Boolean): List<T> {
        return items?.filter(predicate) ?: emptyList()
    }

    fun <R> loadRelatedItemsById(fieldAccessor: (T) -> R?, id: R): List<T> {
        return items?.filter { fieldAccessor(it) == id } ?: emptyList()
    }

    fun deleteItem(predicate: (T) -> Boolean) {
        items?.let { itemList ->
            val itemToRemove = itemList.firstOrNull(predicate)
            itemToRemove?.let {
                itemList.remove(it)
                saveItemsToPrefs(itemList)
            }
        }
    }
    fun <R> loadItemsWhereCollectionContains(fieldAccessor: (T) -> Collection<R>, value: R): List<T> {
        return items?.filter { value in fieldAccessor(it) } ?: emptyList()
    }
    // TODO normalement cette méthode fonctionne si une Collection est passé ou simplement un objet unique
    fun <R> loadRelatedItemsById2(fieldAccessor: (T) -> Any?, id: R): List<T> {
        return items?.filter { item ->
            val fieldValue = fieldAccessor(item)
            when (fieldValue) {
                is Collection<*> -> id in fieldValue
                else -> id == fieldValue
            }
        } ?: emptyList()
    }
}
