package com.delhomme.jobber

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.delhomme.jobber.SignUser.AdditionalInfoActivity
import com.delhomme.jobber.SignUser.User
import com.delhomme.jobber.SignUser.UserProfile
import com.google.gson.Gson
import java.util.Calendar

class ProfileSetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        val nameField = findViewById<EditText>(R.id.name)
        val birthDateField = findViewById<EditText>(R.id.birth_date)
        val datePickerButton = findViewById<Button>(R.id.date_picker_button)
        val emailField = findViewById<EditText>(R.id.email)
        val phoneField = findViewById<EditText>(R.id.phone)
        val submitButton = findViewById<Button>(R.id.submitProfile)

        // Set up DatePicker for birth_date input
        datePickerButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                birthDateField.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear))
            }, year, month, day)

            datePickerDialog.show()
        }

        submitButton.setOnClickListener {
            val name = nameField.text.toString()
            val birthdate = birthDateField.text.toString()
            val email = emailField.text.toString()
            val phone = phoneField.text.toString()

            if (name.isNotEmpty() && birthdate.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()) {
                val userProfile = UserProfile(name, birthdate, email, phone)
                saveUserProfile(email, userProfile)
                goToAdditionalInfoActivity()
            }
        }
    }

    private fun saveUserProfile(email: String, userProfile: UserProfile) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_profile", Gson().toJson(userProfile))
        editor.apply()
    }

    private fun goToAdditionalInfoActivity() {
        val intent = Intent(this, AdditionalInfoActivity::class.java)
        startActivity(intent)
        finish()
    }
}
