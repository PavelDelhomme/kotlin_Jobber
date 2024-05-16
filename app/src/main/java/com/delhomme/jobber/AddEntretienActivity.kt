package com.delhomme.jobber

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.models.Entretien
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddEntretienActivity : AppCompatActivity() {

    private lateinit var etDateEntretien: EditText
    private lateinit var etNotesPreEntretien: EditText
    private lateinit var spinnerTypeEntretien: Spinner
    private lateinit var spinnerModeEntretien: Spinner
    private lateinit var autoCompleteTextViewEntreprise: AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entretien)

        if(getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }
        setupUI()
        setupListeners()

    }


    private fun setupUI() {
        etDateEntretien = findViewById(R.id.dateEntretien)
        etNotesPreEntretien = findViewById(R.id.etNotesEntretien)
        spinnerTypeEntretien = findViewById(R.id.spinner_type_entretien)
        spinnerModeEntretien = findViewById(R.id.spinner_mode_entretien)
        autoCompleteTextViewEntreprise = findViewById(R.id.autoCompleteTextViewEntretien)

        // Setup Spinners for type and style of interviews
        ArrayAdapter.createFromResource(
            this,
            R.array.types_entretien, // Assume you have an array in resources
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTypeEntretien.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.modes_entretien, // Assume you have an array in resources
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerModeEntretien.adapter = adapter
        }
        setupDatePicker()

        // Pré-remplissage et verrouillage du champ entreprise
        val entrepriseId = intent.getStringExtra("ENTREPRISE_ID")
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")
        if (entrepriseId != null || candidatureId != null) {
            val entreprise = entrepriseId?.let { DataRepository(this).getEntrepriseById(it) }
                ?: candidatureId?.let { DataRepository(this).getCandidatureById(it)?.let { candidature -> DataRepository(this).getEntrepriseById(candidature.entrepriseId) } }

            autoCompleteTextViewEntreprise.setText(entreprise?.nom)
            autoCompleteTextViewEntreprise.isEnabled = false
        }
    }

    private fun setupListeners() {
        val btnAddEntretien = findViewById<Button>(R.id.button_add_entretien)
        btnAddEntretien?.setOnClickListener {
            addEntretien()
        }
    }

    private fun setupDatePicker() {
        etDateEntretien.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etDateEntretien.setText(dateFormat.format(selectedDate.time))
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun addEntretien() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataRepository = DataRepository(this)

        val dateEntretien = format.parse(etDateEntretien.text.toString()) ?: Date()
        val typeEntretien = spinnerTypeEntretien.selectedItem.toString()
        val modeEntretien = spinnerModeEntretien.selectedItem.toString()
        val notesPreEntretien = etNotesPreEntretien.text.toString()
        val nomEntreprise = autoCompleteTextViewEntreprise.text.toString()

        val entreprises = dataRepository.loadEntreprises()
        autoCompleteTextViewEntreprise.setAdapter(ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, entreprises.map { it.nom }))

        val entreprise = dataRepository.getOrCreateEntreprise(nomEntreprise)

        intent.getStringExtra("ENTREPRISE_ID")?.let { entrepriseId ->
            autoCompleteTextViewEntreprise.setText(entreprises.indexOfFirst { it.id == entrepriseId })
            autoCompleteTextViewEntreprise.isEnabled = false
        }

        val entretien = Entretien(
            id = UUID.randomUUID().toString(),
            entreprise_id = entreprise.id,
            date_entretien = dateEntretien,
            type = typeEntretien,
            mode = modeEntretien,
            notes_pre_entretien = notesPreEntretien
        )

        dataRepository.saveEntretien(entretien)

        entreprise.entretiens.add(entretien.id)
        dataRepository.saveEntreprise(entreprise)

        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")
        if (candidatureId != null) {
            val candidature = dataRepository.getCandidatureById(candidatureId)
            candidature?.let {
                it.entretiens.add(entretien.id)
                dataRepository.saveCandidature(it)
            }
        }

        Toast.makeText(this, "Entretien ajouté pour ${entreprise.nom}", Toast.LENGTH_SHORT).show()
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