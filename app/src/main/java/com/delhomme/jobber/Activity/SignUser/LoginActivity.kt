package com.delhomme.jobber.Activity.SignUser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import com.delhomme.jobber.Api.LoginResponse
import com.delhomme.jobber.Api.Repository.UserRepository
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        userRepository = UserRepository(this)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val usernameField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)

        loginButton.setOnClickListener {
            val email = usernameField.text.toString()
            val password = passwordField.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
            }
        }
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        Log.d("LoginActivity", "loginUser : email : $email, password : $password")
        userRepository.loginUser(email, password, object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        LocalStorageManager.saveJWT(it.token)
                        Log.d("LoginActivity", "LoginActivity : token saved to Storage")
                        Log.d("LoginActivity", "LoginActivity : starting MainActivity")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Log.e("LoginActivity", "Échec du login : ${response.message()}")
                    Toast.makeText(this@LoginActivity, "Échec du login : ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Erreur réseau : ${t.message}")
                Log.e("LoginActivity", "Erreur réseau : ${t.message}")
                Toast.makeText(this@LoginActivity, "Erreur réseau : ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
