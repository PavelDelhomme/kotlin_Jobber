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


class EducationFragment : Fragment() {
    private lateinit var niveauField: EditText
    private lateinit var intituleField: EditText
    private lateinit var etablissementField: EditText
    private lateinit var startDateFormation: EditText
    private lateinit var endDateFormation: EditText
    private lateinit var localisationField: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_education, container, false)

        niveauField = view.findViewById(R.id.niveau)
        intituleField = view.findViewById(R.id.intitule)
        etablissementField = view.findViewById(R.id.etablissement)
        startDateFormation = view.findViewById(R.id.startDateFormation)
        endDateFormation = view.findViewById(R.id.endDateFormation)
        localisationField = view.findViewById(R.id.localisation)
        saveButton = view.findViewById(R.id.saveEducation)
        cancelButton = view.findViewById(R.id.cancelEducation)

        val educationJson = arguments?.getString("education")
        educationJson?.let {
            val education = Gson().fromJson(it, Education::class.java)
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
                niveau = niveauField.text.toString().trim(),
                intitule = intituleField.text.toString().trim(),
                etablissement = etablissementField.text.toString().trim(),
                dateDebut = startDateFormation.text.toString().trim(),
                dateFin = endDateFormation.text.toString().trim(),
                localisation = localisationField.text.toString().trim(),
                competences = listOf()
            )

            (activity as AdditionalInfoActivity).addEducationToList(education)

            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        cancelButton.setOnClickListener {
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
