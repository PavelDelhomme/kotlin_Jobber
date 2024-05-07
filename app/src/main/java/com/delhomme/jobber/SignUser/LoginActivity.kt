package com.delhomme.jobber.SignUser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            loginWithEmailAndPassword(email, password)
        }

        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun loginWithEmailAndPassword(email: String, password: String) {
        // Cette condition doit être remplacée par la logique d'authentification réelle
        if (email == "paul@delhomme.ovh" && password == "1234") {
            saveUserData(email, password)
            Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Toast.makeText(this, "Connexion échouée !", Toast.LENGTH_SHORT).show()
            Log.e("LoginWithEmailAndPassword", "Erreur de connexion")
        }
    }

    private fun saveUserData(email: String, password: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }
}
