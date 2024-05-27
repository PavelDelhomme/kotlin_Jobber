package com.delhomme.jobber.SignUser

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.DjangoApi.RetrofitClient
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R
import com.delhomme.jobber.Model.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val userProfile = UserProfile(email = "example@example.com", password = "password123")
        RetrofitClient.instance.createUserProfile(userProfile).enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    // L'utilisateur est inscrit avec succès
                    Toast.makeText(this@SignInActivity, "Inscription réussie!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@SignInActivity, "Erreur lors de l'inscription: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Échec de la connexion: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun register(userProfile: UserProfile) {
        RetrofitClient.instance.createUserProfile(userProfile).enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    saveUserData(response.body()!!)
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@SignInActivity, "Inscription échouée : ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Erreur réseau : ${t.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}