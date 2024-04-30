package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.delhomme.jobber.SignUser.User
import com.delhomme.jobber.SignUser.UserProfile
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = getUserData()

        if (user != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    private fun getUserData(): User? {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        val profileJson = sharedPreferences.getString("user_profile", null)

        if (email != null && password != null && profileJson != null){
            val profile = Gson().fromJson(profileJson, UserProfile::class.java)
            return User(email, password, profile)
        }
        return null
    }
}