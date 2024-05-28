package com.delhomme.jobber.Activity.SignUser

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.LoginResponse
import com.delhomme.jobber.Api.Repository.UserRepository
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignInActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        userRepository = UserRepository(this)

        val buttonSignUp = findViewById<Button>(R.id.signInButton)
        val emailField = findViewById<EditText>(R.id.edit_text_email)
        val passwordField = findViewById<EditText>(R.id.edit_text_password)

        buttonSignUp.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            registerUser(email, password)
        }
    }

    private fun registerUser(email: String, password: String) {
        userRepository.registerUser(email, password, object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.token?.let {
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                    } ?: Toast.makeText(this@SignInActivity, "Inscription réussie, mais aucun token recu.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@SignInActivity, "Erreur lors de l'inscription : ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Echec de la connexion : ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
