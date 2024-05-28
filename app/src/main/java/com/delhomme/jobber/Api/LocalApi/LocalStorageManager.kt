package com.delhomme.jobber.Api.LocalApi

import android.content.Context
import android.content.SharedPreferences

object LocalStorageManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("JobberAppPrefs", Context.MODE_PRIVATE)
    }

    fun saveJWT(token: String) {
        sharedPreferences.edit().putString("JWT_TOKEN", token).apply()
    }

    fun getJWT(): String? {
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    fun saveData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }

    // Ajout de la méthode pour effacer une clé spécifique
    fun clearSpecificData(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString("REFRESH_TOKEN", token).apply()
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("REFRESH_TOKEN", null)
    }

    fun clearRefreshToken() {
        sharedPreferences.edit().remove("REFRESH_TOKEN").apply()
    }


}
