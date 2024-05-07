package com.delhomme.jobber.SignUser

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.DashboardFragment
import com.delhomme.jobber.MainActivity

class CheckLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = getUserData()

        if (user != null) {
            startActivity(Intent(this, DashboardFragment::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

    private fun getUserData(): User? {
        // Récupérer les informations utilisateur depuis SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)

        return if (email != null && password != null) {
            User(email, password)
        } else {
            null
        }
    }
}
