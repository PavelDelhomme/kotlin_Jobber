package com.delhomme.jobber.Activity.Candidature

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Utils.CandidatureState
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCandidatureActivity : AppCompatActivity() {
    private lateinit var dataRepository: CandidatureDataRepository
    private lateinit var etDateCandidature: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_candidature)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataRepository = CandidatureDataRepository(applicationContext)
        etDateCandidature = findViewById(R.id.editText_date_candidature)
        setupSpinners()
        setupDatePicker()

        findViewById<Button>(R.id.button_add_candidature).setOnClickListener {
            addCandidature()
        }
        findViewById<Button>(R.id.button_cancel_candidature).setOnClickListener {
            finish()
        }
    }

    private fun setupSpinners() {
        val typePosteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dataRepository.getTypePosteOptions())
        findViewById<Spinner>(R.id.spinner_type_poste).adapter = typePosteAdapter

        val plateformeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dataRepository.getPlateformeOptions())
        findViewById<Spinner>(R.id.spinner_plateforme).adapter = plateformeAdapter
    }

    private fun setupDatePicker() {
        etDateCandidature.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, day, hour, minute)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
                    etDateCandidature.setText(dateFormat.format(selectedDate.time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
        Log.d("AddCandidatureActivityDate", "etDateCandidature setupDatePicker : $etDateCandidature")
    }

    private fun addCandidature() {
        Log.d("AddCandidatureActivityDate", "Attempting to add a new candidature")
        val titreOffre = findViewById<EditText>(R.id.editText_titre_offre).text.toString()
        val nomEntreprise = findViewById<EditText>(R.id.editText_nom_entreprise).text.toString()
        val plateformeUtilisee = findViewById<Spinner>(R.id.spinner_plateforme).selectedItem.toString()
        val typePoste = findViewById<Spinner>(R.id.spinner_type_poste).selectedItem.toString()
        val dateCandidature = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).parse(etDateCandidature.text.toString())!!
        val notesCandidature = findViewById<EditText>(R.id.editText_notes).text.toString()
        val lieuPoste = findViewById<EditText>(R.id.editText_lieuPoste).text.toString()

        val entreprise = EntrepriseDataRepository(applicationContext)

        val newCandidature = Candidature(
            titre_offre = titreOffre,
            entrepriseNom = nomEntreprise,
            type_poste = typePoste,
            plateforme = plateformeUtilisee,
            lieuPoste = lieuPoste,
            state = CandidatureState.CANDIDATEE_ET_EN_ATTENTE,
            notes = notesCandidature,
            date_candidature = dateCandidature
        )

        dataRepository.addOrUpdateCandidature(newCandidature)
        val intentEvent = Intent("com.jobber.EVENEMENT_LIST_UPDATED")
        val intentCandidature = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentCandidature)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentEvent)
        finish()
    }
}
