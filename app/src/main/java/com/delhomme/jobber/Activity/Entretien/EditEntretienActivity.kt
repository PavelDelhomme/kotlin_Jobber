package com.delhomme.jobber.Activity.Entretien

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
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class EditEntretienActivity : AppCompatActivity() {

    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var entretienDataRepository: EntretienDataRepository

    private lateinit var etDateEntretien: EditText
    private lateinit var etNotesPreEntretien: EditText
    private lateinit var etNotesPostEntretien: EditText
    private lateinit var spinnerTypeEntretien: Spinner
    private lateinit var spinnerModeEntretien: Spinner
    private lateinit var autoCompleteTextViewEntreprise: AutoCompleteTextView
    private lateinit var autocompleteTextViewContact: AutoCompleteTextView
    private var entretienId: String? = null
    private var candidatureId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_entretien)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRepositories()
        setupUI()
        setupListeners()

        entretienId = intent.getStringExtra("ENTRETIEN_ID")
        candidatureId = intent.getStringExtra("CANDIDATURE_ID")

        entretienId?.let {
            setupFields(entretienId!!)
        }
    }

    private fun initRepositories() {
        entrepriseDataRepository = EntrepriseDataRepository(this)
        contactDataRepository = ContactDataRepository(this)
        entretienDataRepository = EntretienDataRepository(this)
    }

    private fun setupUI() {
        etDateEntretien = findViewById(R.id.dateEntretien)
        etNotesPreEntretien = findViewById(R.id.etNotesPreEntretien)
        etNotesPostEntretien = findViewById(R.id.etNotesPostEntretien)
        spinnerTypeEntretien = findViewById(R.id.spinner_type_entretien)
        spinnerModeEntretien = findViewById(R.id.spinner_mode_entretien)
        autoCompleteTextViewEntreprise = findViewById(R.id.autoCompleteTextViewEntretien)
        autoCompleteTextViewEntreprise.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedEntreprise = adapterView.getItemAtPosition(position) as String
            updateContactsForSelectedEntreprise(selectedEntreprise)
        }
        autocompleteTextViewContact = findViewById(R.id.autoCompleteTextViewContact)
        setupContactAutoComplete()


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
        findViewById<Button>(R.id.btnCancelEntretienChanges).setOnClickListener {
            finish()
            Toast.makeText(this, "Annulation de la création de l'entretien...", Toast.LENGTH_SHORT).show()
        }

        setupEntrepriseAutoComplete()
        setupDateTimePicker()
    }

    private fun setupFields(entretienId: String) {
        val entretien = entretienDataRepository.findByCondition { it.id == entretienId }.firstOrNull()
        entretien?.let {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
            etDateEntretien.setText(dateFormat.format(it.date_entretien))
            etNotesPreEntretien.setText(it.notes_pre_entretien)
            etNotesPostEntretien.setText(it.notes_post_entretien)
            autoCompleteTextViewEntreprise.setText(it.entrepriseNom)

            updateContactsForSelectedEntreprise(it.entrepriseNom)
            autocompleteTextViewContact.setText("${it.contact?.prenom} ${it.contact?.nom}")

            spinnerTypeEntretien.setSelection(resources.getStringArray(R.array.types_entretien).indexOf(it.type))
            spinnerModeEntretien.setSelection(resources.getStringArray(R.array.modes_entretien).indexOf(it.mode))
        }
    }
    private fun setupEntrepriseAutoComplete() {
        val entreprises = entrepriseDataRepository.loadEntreprises().map { it.nom }
        val entrepriseAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises)
        autoCompleteTextViewEntreprise.setAdapter(entrepriseAdapter)
    }

    private fun setupContactAutoComplete() {
        autoCompleteTextViewEntreprise.setOnItemClickListener { _, _, position, _ ->
            val selectedEntreprise = autoCompleteTextViewEntreprise.adapter.getItem(position) as String
            val contacts = contactDataRepository.loadContactsForEntreprise(selectedEntreprise)
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, contacts.map { it.getFullName() })
            autocompleteTextViewContact.setAdapter(adapter)
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

    private fun setupListeners() {
        findViewById<Button>(R.id.button_save_entretien_changes).setOnClickListener {
            saveEntretienChanges()
        }
    }
    private fun updateContactsForSelectedEntreprise(entrepriseNom: String) {
        val contacts = contactDataRepository.loadContactsForEntreprise(entrepriseNom)
        val contactNames = contacts.map { "${it.prenom} ${it.nom} - ${it.entrepriseNom}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, contactNames)
        autocompleteTextViewContact.setAdapter(adapter)
    }


    private fun saveEntretienChanges() {

        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
        val dateEntretien = format.parse(etDateEntretien.text.toString()) ?: Date()
        val typeEntretien = spinnerTypeEntretien.selectedItem.toString()
        val modeEntretien = spinnerModeEntretien.selectedItem.toString()
        val notesPreEntretien = etNotesPreEntretien.text.toString()
        val notesPostEntretien = etNotesPostEntretien.text.toString()
        val nomEntreprise = autoCompleteTextViewEntreprise.text.toString()
        val contactInfo = autocompleteTextViewContact.text.toString().split(" ")

        val entreprise = entrepriseDataRepository.getOrCreateEntreprise(nomEntreprise)
        val contact = contactDataRepository.getOrCreateContact(contactInfo[0], contactInfo[1], nomEntreprise)

        val updatedEntretien = Entretien(
            id = entretienId ?:UUID.randomUUID().toString(),
            date_entretien = dateEntretien,
            type = typeEntretien,
            mode = modeEntretien,
            notes_pre_entretien = notesPreEntretien,
            notes_post_entretien = notesPostEntretien,
            entrepriseNom = entreprise.nom,
            contact_id = contact.id,
            contact = contact,
            candidature_id = candidatureId ?: ""
        )

        entretienDataRepository.updateOrAddItem(entretienDataRepository.getItems().toMutableList(), updatedEntretien)
        Toast.makeText(this, "Entretien mis à jour avec succès.", Toast.LENGTH_SHORT).show()
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("ENTRETIENS_UPDATED"))
        finish()
    }

}
