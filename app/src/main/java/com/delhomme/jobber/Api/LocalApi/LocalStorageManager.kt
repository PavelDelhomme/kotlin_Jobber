package com.delhomme.jobber.Api.LocalApi

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.auth0.android.jwt.JWT

object LocalStorageManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("JobberAppPrefs", Context.MODE_PRIVATE)
        Log.d("LocalStorageManager", "initialiaze : shared¨Preferences: $sharedPreferences")
    }

    fun saveJWT(token: String) {
        sharedPreferences.edit().putString("JWT_TOKEN", token).apply()
        Log.d("LocalStorageManager", "saveJWT : JWT_TOKEN : ${sharedPreferences.getString("JWT_TOKEN", "Non trouvé")} saved")
    }

    fun getJWT(): String? {
        Log.d("LocalStorageManager", "getJWT : JWT_TOKEN: ${sharedPreferences.getString("JWT_TOKEN", "Non trouvé")}")
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    fun saveData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
        Log.d("LocalStorageManager", "saveData : data saved : $key, $value")
    }

    fun getData(key: String): String? {
        Log.d("LocalStorageManager", "getData : key : $key, datas getted : ${sharedPreferences.getString(key, null)}")
        return sharedPreferences.getString(key, null)
    }

    fun clearData() {
        Log.d("LocalStorageManager", "clearData : data cleared.")
        sharedPreferences.edit().clear().apply()
    }

    // Ajout de la méthode pour effacer une clé spécifique
    fun clearSpecificData(key: String) {
        Log.d("LocalStorageManager", "clearSpecificData : key: $key, data cleared")
        sharedPreferences.edit().remove(key).apply()
    }

    fun saveRefreshToken(token: String) {
        Log.d("LocalStorageManager", "saveRefreshToken, token : $token")
        sharedPreferences.edit().putString("REFRESH_TOKEN", token).apply()
    }

    fun getRefreshToken(): String? {
        Log.d("LocalStorageManager", "getRefreshToken, refresh_token: ${sharedPreferences.getString("REFRESH_TOKEN", "No token refresh getted")}")
        return sharedPreferences.getString("REFRESH_TOKEN", null)
    }

    fun clearRefreshToken() {
        Log.d("LocalStorageManager", "clearRefreshToken")
        sharedPreferences.edit().remove("REFRESH_TOKEN").apply()
    }

    fun isTokenValid(): Boolean {
        val token = getJWT()
        if (token.isNullOrEmpty()) {
            Log.d("LocalStorageManager", "isTokenValid : token is null or empty !")
            return false
        }
        return try {
            val jwt = JWT(token)
            Log.d("LocalStorageManager", "isTokenValid : try : JWT(token) : $jwt")
            Log.d("LocalStorageManager", "${!jwt.isExpired(10)}")
            !jwt.isExpired(10)
        } catch (e: Exception) {
            Log.d("LocalStorageManager", "isTokenValid : catch : e")
            Log.e("LocalStorageManager", "$e")
            false
        }
    }

}
