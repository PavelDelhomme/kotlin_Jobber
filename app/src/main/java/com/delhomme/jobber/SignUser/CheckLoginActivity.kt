package com.delhomme.jobber.SignUser

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.DashboardActivity
import com.delhomme.jobber.WelcomeActivity

class CheckLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = getUserData()

        if (user != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
        } else {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
        finish()
    }

    private fun getUserData(): User? {
        // Retourner les données de l'utilisateur depuis la mémoire persistante
        return null // Placeholder : implémentez la logique pour lire les données
    }
}