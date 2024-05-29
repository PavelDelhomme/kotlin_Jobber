package com.delhomme.jobber.Activity.Candidature

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.R
import com.delhomme.jobber.Utils.CandidatureState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditCandidatureActivity : AppCompatActivity() {
    private lateinit var candidatureDataRepository: CandidatureDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository

    private var candidatureId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_candidature)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        candidatureDataRepository = CandidatureDataRepository(this)
        entrepriseDataRepository = EntrepriseDataRepository(this)
        candidatureId = intent.getStringExtra("CANDIDATURE_ID")

        if (candidatureId == null) {
            Toast.makeText(this, "Erreur: ID de candidature manquant.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupSpinners()
        setupEntrepriseAutoComplete()
        setupFields()

        findViewById<Button>(R.id.btnSaveCandidatureChanges).setOnClickListener {
            saveChanges()
        }
        findViewById<Button>(R.id.btnCancelCandidatureChanges).setOnClickListener {
            cancelChanges()
        }
    }

    private fun setupEntrepriseAutoComplete() {
        val entreprises = entrepriseDataRepository.getItems().map { it.nom }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises)
        findViewById<AutoCompleteTextView>(R.id.actvNomEntreprise).apply {
            setAdapter(adapter)
        }
    }

    private fun setupDatePicker(date: Date) {
        val dateEditText = findViewById<EditText>(R.id.etDateCandidature)
        dateEditText.setText(SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(date))

        dateEditText.setOnClickListener {
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                dateEditText.setText(SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(calendar.time))
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupSpinners() {
        val typePosteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, candidatureDataRepository.getTypePosteOptions())
        findViewById<Spinner>(R.id.spinner_type_poste).adapter = typePosteAdapter

        val plateformeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, candidatureDataRepository.getPlateformeOptions())
        findViewById<Spinner>(R.id.spinner_plateforme).adapter = plateformeAdapter

        val stateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, CandidatureState.values().map { it.name.replace("_", " ").toLowerCase().capitalize() })
        findViewById<Spinner>(R.id.spinnerState).adapter = stateAdapter
    }

    private fun setupFields() {
        candidatureId?.let {
            val candidature = candidatureDataRepository.getCandidatureById(it)
            candidature?.let { cand ->
                findViewById<EditText>(R.id.etTitreOffre).setText(cand.titre_offre)
                findViewById<EditText>(R.id.etNotes).setText(cand.notes)
                setupDatePicker(cand.date_candidature)
                findViewById<AutoCompleteTextView>(R.id.actvNomEntreprise).setText(cand.entrepriseNom)
                findViewById<Spinner>(R.id.spinner_plateforme).setSelection((findViewById<Spinner>(R.id.spinner_plateforme).adapter as ArrayAdapter<String>).getPosition(cand.plateforme))
                findViewById<Spinner>(R.id.spinner_type_poste).setSelection((findViewById<Spinner>(R.id.spinner_type_poste).adapter as ArrayAdapter<String>).getPosition(cand.type_poste))
                findViewById<EditText>(R.id.etLieuPoste).setText(cand.lieuPoste)
                findViewById<Spinner>(R.id.spinnerState).setSelection(cand.state.ordinal)
                findViewById<EditText>(R.id.etNotes).setText(cand.notes)
            }
        }
    }


    private fun saveChanges() {
        val titre = findViewById<EditText>(R.id.etTitreOffre).text.toString()
        val entrepriseNom = findViewById<AutoCompleteTextView>(R.id.actvNomEntreprise).text.toString()
        val entreprise = entrepriseDataRepository.getOrCreateEntreprise(entrepriseNom)
        val notes = findViewById<EditText>(R.id.etNotes).text.toString()
        val plateforme = findViewById<Spinner>(R.id.spinner_plateforme).selectedItem.toString()
        val typePoste = findViewById<Spinner>(R.id.spinner_type_poste).selectedItem.toString()
        val lieuPoste = findViewById<EditText>(R.id.etLieuPoste).text.toString()
        val dateCandidature = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).parse(findViewById<EditText>(R.id.etDateCandidature).text.toString())!!
        val state = CandidatureState.values()[findViewById<Spinner>(R.id.spinnerState).selectedItemPosition]

        val updatedCandidature = Candidature(
            id = candidatureId!!,
            titre_offre = titre,
            entrepriseNom = entreprise.nom,
            date_candidature = dateCandidature,
            plateforme = plateforme,
            type_poste = typePoste,
            lieuPoste = lieuPoste,
            state = state,
            notes = notes
        )


        candidatureDataRepository.addOrUpdateCandidature(updatedCandidature)

        Toast.makeText(this, "Candidature mise à jour avec succès", Toast.LENGTH_SHORT).show()
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("com.jobber.CANDIDATURE_LIST_UPDATED"))
        //LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("com.jobber.ENTREPRISE_LIST_UPDATED"))
        finish()
    }

    private fun cancelChanges() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}