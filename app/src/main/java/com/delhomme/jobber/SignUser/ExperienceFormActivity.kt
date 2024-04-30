package com.delhomme.jobber.SignUser

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.R
import com.google.gson.Gson
import java.util.Calendar

class ExperienceFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_experience_form)

        val titleField = findViewById<EditText>(R.id.title)
        val companyField = findViewById<EditText>(R.id.company)
        val startDateField = findViewById<EditText>(R.id.startDate)
        val endDateField = findViewById<EditText>(R.id.endDate)
        val locationField = findViewById<EditText>(R.id.location)
        val saveButton = findViewById<Button>(R.id.saveExperience)

        // Si des données d'une expérience existante sont transmises, les remplir
        intent.getStringExtra("experience")?.let { experienceJson ->
            val experience = Gson().fromJson(experienceJson, Experience::class.java)
            titleField.setText(experience.titre)
            companyField.setText(experience.entreprise)
            startDateField.setText(experience.dateDebut)
            endDateField.setText(experience.dateFin)
            locationField.setText(experience.lieu)
        }

        startDateField.setOnClickListener { showDatePickerDialog(startDateField) }
        endDateField.setOnClickListener { showDatePickerDialog(endDateField) }

        saveButton.setOnClickListener {
            val experience = Experience(
                titleField.text.toString().trim(),
                companyField.text.toString().trim(),
                startDateField.text.toString().trim(),
                endDateField.text.toString().trim(),
                locationField.text.toString().trim(),
                listOf(),
                listOf()
            )

            val intent = Intent()
            intent.putExtra("experience", Gson().toJson(experience))
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            editText.setText(date)
        }, year, month, day)

        datePickerDialog.show()
    }
}
