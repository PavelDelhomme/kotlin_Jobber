package com.delhomme.jobber.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Activity.SignUser.LoginActivity
import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import com.delhomme.jobber.Fragment.FragmentDashboard
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.Model.User
// import com.google.gson.Gson

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalStorageManager.initialize(this)


        val token = LocalStorageManager.getJWT()
        if (token != null && token.isNotEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val user = getUserData()

        if (user != null) {
            startActivity(Intent(this, FragmentDashboard::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

    private fun getUserData(): User? {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        // val name = sharedPreferences.getString("name", null)
        // val phone = sharedPreferences.getString("telephone", null)
        val password = sharedPreferences.getString("password", null)
        val profileJson = sharedPreferences.getString("user_profile", null)

        if (email != null && password != null && profileJson != null) {
            //val profile = Gson().fromJson(profileJson, UserProfile::class.java)
            // val profile = UserProfile(name, email, phone)
            return User(email, password)
        }
        return null
    }
}

