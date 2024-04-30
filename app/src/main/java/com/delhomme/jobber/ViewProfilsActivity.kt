package com.delhomme.jobber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.delhomme.jobber.SignUser.Education
import com.delhomme.jobber.SignUser.Experience
import com.delhomme.jobber.SignUser.Skill
import com.delhomme.jobber.SignUser.User
import com.delhomme.jobber.SignUser.UserProfile
import com.google.gson.Gson
import kotlin.math.exp


class ViewProfilsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profils)

        val user = getUserData()

        if (user != null) {
            val nameTextView = findViewById<TextView>(R.id.nameTextView)
            val birthDateTextView = findViewById<TextView>(R.id.birthDateTextView)

            nameTextView.text = user.profile.name
            birthDateTextView.text = user.profile.birthDate

            displayAdditionalInfo(user)
        }

        val editButton = findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            val intent = Intent(this, ProfileSetupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getUserData(): User? {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        val profileJson = sharedPreferences.getString("user_profile", null)

        if (email != null && password != null && profileJson != null) {
            val profile = Gson().fromJson(profileJson, UserProfile::class.java)
            return User(email, password, profile)
        }
        return null
    }

    private fun displayAdditionalInfo(user: User) {
        val experienceContainer = findViewById<LinearLayout>(R.id.experienceContainer)
        val skillContainer = findViewById<LinearLayout>(R.id.skillContainer)
        val educationContainer = findViewById<LinearLayout>(R.id.educationContainer)

        for (experience in user.profile.experiences) {
            val expTextView = TextView(this).apply {
                text = "${experience.titre} à ${experience.entreprise}, ${experience.dateDebut} - ${experience.dateFin}"
            }
            experienceContainer.addView(expTextView)
        }

        for (skill in user.profile.skills) {
            val skillTextView = TextView(this).apply {
                text = "${skill.categorie}: ${skill.nom}"
            }
            skillContainer.addView(skillTextView)
        }

        for (education in user.profile.educations) {
            val eduTextView = TextView(this).apply {
                text = "${education.niveau} à ${education.etablissement}, ${education.dateDebut} - ${education.dateFin}"
            }
            educationContainer.addView(eduTextView)
        }
    }
}