package com.delhomme.jobber.SignUser

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.delhomme.jobber.R
import com.google.gson.Gson
import java.util.Calendar

class ExperienceFragment : Fragment() {
    private lateinit var titleField: EditText
    private lateinit var companyField: EditText
    private lateinit var startDateField: EditText
    private lateinit var endDateField: EditText
    private lateinit var locationField: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_experience, container, false)

        titleField = view.findViewById(R.id.title)
        companyField = view.findViewById(R.id.company)
        startDateField = view.findViewById(R.id.startDate)
        endDateField = view.findViewById(R.id.endDate)
        locationField = view.findViewById(R.id.location)
        saveButton = view.findViewById(R.id.saveExperience)

        val experienceJson = arguments?.getString("experience")
        experienceJson?.let {
            val experience = Gson().fromJson(it, Experience::class.java)
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
                titre = titleField.text.toString().trim(),
                entreprise = companyField.text.toString().trim(),
                dateDebut = startDateField.text.toString().trim(),
                dateFin = endDateField.text.toString().trim(),
                lieu = locationField.text.toString().trim(),
                competences = listOf(),
                missions = listOf()
            )

            (activity as AdditionalInfoActivity).addExperienceToList(experience)

            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        return view
    }


    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            editText.setText(date)
        }, year, month, day)

        datePickerDialog.show()
    }

}