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
import com.delhomme.jobber.CandidatureState
import com.delhomme.jobber.Entreprise.model.Entreprise
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Model.Entreprise
import com.delhomme.jobber.R
import com.delhomme.jobber.Utils.CandidatureState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCandidatureActivity : AppCompatActivity() {
    private lateinit var candidatureDataRepository: CandidatureDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var etDateCandidature: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_candidature)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        candidatureDataRepository = CandidatureDataRepository(applicationContext)
        entrepriseDataRepository = EntrepriseDataRepository(applicationContext)
        etDateCandidature = findViewById(R.id.editText_date_candidature)

        setupSpinners()
        setupDatePicker()

        findViewById<Button>(R.id.button_add_candidature).setOnClickListener {
            handleEntrepriseAndCandidature()
        }
        findViewById<Button>(R.id.button_cancel_candidature).setOnClickListener {
            finish()
        }
    }

    private fun handleEntrepriseAndCandidature() {
        val nomEntreprise = findViewById<EditText>(R.id.editText_nom_entreprise).text.toString()
        val entreprise = entrepriseDataRepository.getOrCreateEntreprise(nomEntreprise)

        addCandidature(entreprise)
    }
    private fun setupSpinners() {
        val typePosteOptions = resources.getStringArray(R.array.type_poste_options)
        val typePosteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, typePosteOptions)
        findViewById<Spinner>(R.id.spinner_type_poste).adapter = typePosteAdapter

        val plateformeOptions = resources.getStringArray(R.array.plateforme_options)
        val plateformeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, plateformeOptions)
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

    private fun addCandidature(entreprise: Entreprise) {
        Log.d("AddCandidatureActivityDate", "Attempting to add a new candidature")
        val titreOffre = findViewById<EditText>(R.id.editText_titre_offre).text.toString()
        val plateformeUtilisee = findViewById<Spinner>(R.id.spinner_plateforme).selectedItem.toString()
        val typePoste = findViewById<Spinner>(R.id.spinner_type_poste).selectedItem.toString()
        val dateCandidature = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).parse(etDateCandidature.text.toString())!!
        val notesCandidature = findViewById<EditText>(R.id.editText_notes).text.toString()
        val lieuPoste = findViewById<EditText>(R.id.editText_lieuPoste).text.toString()


        val newCandidature = Candidature(
            titre_offre = titreOffre,
            entreprise = entreprise.nom,
            type_poste = typePoste,
            plateforme = plateformeUtilisee,
            lieu_poste = lieuPoste,
            state = CandidatureState.CANDIDATEE_ET_EN_ATTENTE,
            notes = notesCandidature,
            date_candidature = dateCandidature
        )

        candidatureDataRepository.addOrUpdateCandidature(newCandidature)
        val intentEvent = Intent("com.jobber.EVENEMENT_LIST_UPDATED")
        val intentCandidature = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentCandidature)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentEvent)
        finish()
    }
}
