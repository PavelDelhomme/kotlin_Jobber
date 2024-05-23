package com.delhomme.jobber.Entretien

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditEntretienActivity : AppCompatActivity() {

    private lateinit var etDateEntretien: EditText
    private lateinit var etNotesPreEntretien: EditText
    private lateinit var etNotesPostEntretien: EditText
    private lateinit var spinnerTypeEntretien: Spinner
    private lateinit var spinnerModeEntretien: Spinner
    private lateinit var autoCompleteTextViewEntreprise: AutoCompleteTextView
    private lateinit var spinnerContact: Spinner
    private var entretienId: String? = null
    private lateinit var dataRepository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_entretien)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataRepository = DataRepository(this)
        entretienId = intent.getStringExtra("ENTRETIEN_ID")

        setupUI()
        setupListeners()
        setupFields()
        findViewById<Button>(R.id.btnCancelEntretienChanges).setOnClickListener {
            cancelChange()
        }
    }

    private fun setupUI() {
        etDateEntretien = findViewById(R.id.dateEntretien)
        etNotesPreEntretien = findViewById(R.id.etNotesPreEntretien)
        etNotesPostEntretien = findViewById(R.id.etNotesPostEntretien)
        spinnerTypeEntretien = findViewById(R.id.spinner_type_entretien)
        spinnerModeEntretien = findViewById(R.id.spinner_mode_entretien)
        autoCompleteTextViewEntreprise = findViewById(R.id.autoCompleteTextViewEntretien)
        spinnerContact = findViewById(R.id.spinnerContact)
        autoCompleteTextViewEntreprise.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedEntreprise = adapterView.getItemAtPosition(position) as String
            updateContactsForSelectedEntreprise(selectedEntreprise)
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.types_entretien,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTypeEntretien.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.modes_entretien,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerModeEntretien.adapter = adapter
        }

        setupDateTimePicker()

        val entreprises = dataRepository.getEntreprises()
        val entrepriseAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises.map { it.nom })
        autoCompleteTextViewEntreprise.setAdapter(entrepriseAdapter)
    }

    private fun updateContactsForSelectedEntreprise(entrepriseNom: String) {
        val contacts = dataRepository.getContactsForEntreprise(entrepriseNom)
        val contactNames = mutableListOf("Aucun contact") // Ajoute l'option par défaut
        if (contacts != null) {
            contactNames.addAll(contacts.map { "${it.prenom} ${it.nom}" })
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contactNames)
        spinnerContact.adapter = adapter
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.button_save_entretien_changes).setOnClickListener {
            saveEntretienChanges()
        }
    }

    private fun setupDateTimePicker() {
        etDateEntretien.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, day, hour, minute)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
                    etDateEntretien.setText(dateFormat.format(selectedDate.time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupFields() {
        entretienId?.let {
            dataRepository.getEntretienById(it)?.let { entretien ->
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
                etDateEntretien.setText(dateFormat.format(entretien.date_entretien))
                etNotesPreEntretien.setText(entretien.notes_pre_entretien)
                etNotesPostEntretien.setText(entretien.notes_post_entretien)
                autoCompleteTextViewEntreprise.setText(entretien.entrepriseNom)
                updateContactsForSelectedEntreprise(entretien.entrepriseNom)

                val contactIndex =
                    dataRepository.getContactsForEntreprise(entretien.entrepriseNom)?.indexOfFirst { it.id == entretien.contact_id }
                        ?.plus(1)
                spinnerContact.setSelection(contactIndex!!)
                spinnerTypeEntretien.setSelection(resources.getStringArray(R.array.types_entretien).indexOf(entretien.type))
                spinnerModeEntretien.setSelection(resources.getStringArray(R.array.modes_entretien).indexOf(entretien.mode))
            }
        }
    }

    private fun saveEntretienChanges() {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
        val dateEntretien = format.parse(etDateEntretien.text.toString()) ?: Date()
        val typeEntretien = spinnerTypeEntretien.selectedItem.toString()
        val modeEntretien = spinnerModeEntretien.selectedItem.toString()
        val notesPreEntretien = etNotesPreEntretien.text.toString()
        val notesPostEntretien = etNotesPostEntretien.text.toString()
        val nomEntreprise = autoCompleteTextViewEntreprise.text.toString()
        val nomContact = spinnerContact.selectedItem.toString()

        val entreprise = dataRepository.getOrCreateEntreprise(nomEntreprise)
        val contact = dataRepository.getOrCreateContact(nomContact.split(" ")[0], nomContact.split(" ")[1], entreprise.nom)


        if (entretienId != null) {
            val entretien = dataRepository.getEntretienById(entretienId!!)!!
            dataRepository.editEntretien(
                entretien.id,
                entreprise.nom,
                contact.id,
                entretien.candidature_id,
                dateEntretien,
                typeEntretien,
                modeEntretien,
                notesPreEntretien,
                notesPostEntretien
            )
            Toast.makeText(this, "Entretien mis à jour avec succès.", Toast.LENGTH_SHORT).show()
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("ENTRETIENS_UPDATED"))
            finish()
        } else {
            Toast.makeText(this, "Erreur : ID de l'entretien manquant", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelChange() {
        finish()
    }
}
