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
    private lateinit var etNomEntretien: EditText
    private lateinit var etNotesPreEntretien: EditText
    private lateinit var spinnerTypeEntretien: Spinner
    private lateinit var spinnerStyleEntretien: Spinner
    private lateinit var autoCompleteTextViewEntreprise: AutoCompleteTextView
    private lateinit var button_add_entretien: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entretien)

        if(getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }
        initUI()
        setupListeners()

    }


    private fun initUI() {
        etDateEntretien = findViewById(R.id.dateEntretien)
        etNotesPreEntretien = findViewById(R.id.etNotesEntretien)
        spinnerTypeEntretien = findViewById(R.id.spinner_type_entretien)
        spinnerStyleEntretien = findViewById(R.id.spinner_style_entretien)
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
            R.array.styles_entretien, // Assume you have an array in resources
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStyleEntretien.adapter = adapter
        }

        // Setup DatePicker
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

    private fun setupListeners() {
        val btnAddEntretien = findViewById<Button>(R.id.button_add_entretien)
        btnAddEntretien?.setOnClickListener {
            addEntretien()
        }
    }

    private fun addEntretien() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataRepository = DataRepository(this)

        val dateEntretien = format.parse(etDateEntretien.text.toString()) ?: Date()
        val typeEntretien = spinnerTypeEntretien.selectedItem.toString()
        val styleEntretien = spinnerStyleEntretien.selectedItem.toString()
        val notesPreEntretien = etNotesPreEntretien.text.toString()
        val nomEntreprise = autoCompleteTextViewEntreprise.text.toString()

        val entreprise = dataRepository.getOrCreateEntreprise(nomEntreprise)

        val entretien = Entretien(
            id = UUID.randomUUID().toString(),
            entreprise_id = entreprise.id,
            entrepriseNom = entreprise.nom,
            date_entretien = dateEntretien,
            type_entretien = typeEntretien,
            style_entretien = styleEntretien,
            notes_pre_entretien = notesPreEntretien
        )

        dataRepository.saveEntretien(entretien)

        entreprise.entretiens.add(entretien)
        dataRepository.saveEntreprise(entreprise)

        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")
        if (candidatureId != null) {
            val candidature = dataRepository.getCandidatureById(candidatureId)
            candidature?.let {
                it.entretiens.add(entretien)
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