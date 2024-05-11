package com.delhomme.jobber

import android.content.Context
import com.delhomme.jobber.models.Candidature
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
}