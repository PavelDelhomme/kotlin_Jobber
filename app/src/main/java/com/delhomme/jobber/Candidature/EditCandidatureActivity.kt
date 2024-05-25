package com.delhomme.jobber.Candidature

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditCandidatureActivity : AppCompatActivity() {
    private lateinit var dataRepository: DataRepository
    private var candidatureId: String? = null
    private lateinit var candidature: Candidature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_candidature)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        dataRepository = DataRepository(applicationContext)

        candidature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("CANDIDATURE_KEY", Candidature::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("CANDIDATURE_KEY")!!
        }

        if (!::candidature.isInitialized) { // Vérification si la candidature est initialisée
            Toast.makeText(this, "Erreur: Candidature manquante.", Toast.LENGTH_LONG).show()
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
        val entreprises = dataRepository.getEntreprises().map { it.nom }
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
        val typePosteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dataRepository.getTypePosteOptions())
        findViewById<Spinner>(R.id.spinner_type_poste).adapter = typePosteAdapter

        val plateformeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dataRepository.getPlateformeOptions())
        findViewById<Spinner>(R.id.spinner_plateforme).adapter = plateformeAdapter
    }

    private fun setupFields() {
        candidatureId?.let {
            val candidature = dataRepository.getCandidatureById(it)
            candidature?.let { cand ->
                findViewById<EditText>(R.id.etTitreOffre).setText(cand.titre_offre)
                findViewById<EditText>(R.id.etNotes).setText(cand.notes)
                setupDatePicker(cand.date_candidature)
                findViewById<AutoCompleteTextView>(R.id.actvNomEntreprise).setText(dataRepository.getEntrepriseByNom(cand.entrepriseNom)?.nom)
                findViewById<Spinner>(R.id.spinner_plateforme).setSelection(dataRepository.getPlateformeOptions().indexOf(cand.plateforme))
                findViewById<Spinner>(R.id.spinner_type_poste).setSelection(dataRepository.getTypePosteOptions().indexOf(cand.type_poste))
                findViewById<EditText>(R.id.etLieuPoste).setText(cand.lieuPoste)
                findViewById<EditText>(R.id.etNotes).setText(cand.notes)

            }
        }
    }

    private fun saveChanges() {
        val titre = findViewById<EditText>(R.id.etTitreOffre).text.toString()
        val entrepriseNom = findViewById<AutoCompleteTextView>(R.id.actvNomEntreprise).text.toString()
        val entrepriseId = dataRepository.getOrCreateEntreprise(entrepriseNom).nom
        val notes = findViewById<EditText>(R.id.etNotes).text.toString()
        val plateforme = findViewById<Spinner>(R.id.spinner_plateforme).selectedItem.toString()
        val typePoste = findViewById<Spinner>(R.id.spinner_type_poste).selectedItem.toString()
        val lieuPoste = findViewById<EditText>(R.id.etLieuPoste).text.toString()
        val dateCandidature = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).parse(findViewById<EditText>(R.id.etDateCandidature).text.toString())!!

        val existingCandidature = candidatureId?.let { dataRepository.getCandidatureById(it) }
        val entretiensIds = existingCandidature?.entretiens ?: mutableListOf()
        val appelsIds = existingCandidature?.appels ?: mutableListOf()
        val relancesIds = existingCandidature?.relances ?: mutableListOf()
        val stateCandidature = existingCandidature?.state

        if (candidatureId != null) {
            dataRepository.editCandidature(
                candidatureId!!,
                titre,
                stateCandidature!!,
                notes,
                plateforme,
                typePoste,
                lieuPoste,
                entrepriseId,
                dateCandidature,
                entretiensIds,
                appelsIds,
                relancesIds
            )
            Toast.makeText(this, "Candidature mise à jour avec succès", Toast.LENGTH_SHORT).show()
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("com.jobber.CANDIDATURE_LIST_UPDATED"))
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("com.jobber.ENTREPRISE_LIST_UPDATED"))
            finish()
        } else {
            Toast.makeText(this, "Erreur : ID de candidature manquant", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelChanges() {
        finish()
    }
}