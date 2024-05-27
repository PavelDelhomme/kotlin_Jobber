package com.delhomme.jobber.Api.LocalApi

import android.content.Context
import android.content.SharedPreferences

class LocalStorageManager(val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("JobberAppPrefs", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }

}