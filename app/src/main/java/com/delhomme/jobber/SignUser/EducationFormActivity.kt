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

class EducationFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_education_form)

        val niveauField = findViewById<EditText>(R.id.niveau)
        val intituleField = findViewById<EditText>(R.id.intitule)
        val etablissementField = findViewById<EditText>(R.id.etablissement)
        val startDateFormation = findViewById<EditText>(R.id.startDateFormation)
        val endDateFormation = findViewById<EditText>(R.id.endDateFormation)
        val localisationField = findViewById<EditText>(R.id.localisation)
        val saveButton = findViewById<Button>(R.id.saveEducation)

        intent.getStringExtra("education")?.let { educationJson ->
            val education = Gson().fromJson(educationJson, Education::class.java)
            niveauField.setText(education.niveau)
            intituleField.setText(education.intitule)
            etablissementField.setText(education.etablissement)
            startDateFormation.setText(education.dateDebut)
            endDateFormation.setText(education.dateFin)
            localisationField.setText(education.localisation)
        }

        startDateFormation.setOnClickListener { showDatePickerDialog(startDateFormation) }
        endDateFormation.setOnClickListener { showDatePickerDialog(endDateFormation) }

        saveButton.setOnClickListener {
            val education = Education(
                niveauField.text.toString().trim(),
                intituleField.text.toString().trim(),
                etablissementField.text.toString().trim(),
                startDateFormation.text.toString().trim(),
                endDateFormation.text.toString().trim(),
                localisationField.text.toString().trim(),
                listOf()
            )

            val intent = Intent()
            intent.putExtra("education", Gson().toJson(education))
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
