package com.delhomme.jobber.Activity.Entretien

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.Model.Contact
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
    private lateinit var selectedContacts: MutableList<Contact>
    private lateinit var etDateEntretien: EditText
    private lateinit var etNotesPreEntretien: EditText
    private lateinit var etNotesPostEntretien: EditText
    private lateinit var spinnerTypeEntretien: Spinner
    private lateinit var spinnerModeEntretien: Spinner
    private lateinit var autoCompleteTextViewEntrepriseEntretien: AutoCompleteTextView
    private lateinit var multiAutoCompleteTextViewContacts: MultiAutoCompleteTextView
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
            setupFields(it)
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
        autoCompleteTextViewEntrepriseEntretien = findViewById(R.id.autoCompleteTextViewEntrepriseEntretien)
        multiAutoCompleteTextViewContacts = findViewById(R.id.multiAutoCompleteTextViewContacts)

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
        setupContactAutoComplete()
    }

    private fun setupFields(entretienId: String) {
        val entretien = entretienDataRepository.findByCondition { it.id == entretienId }.firstOrNull()
        entretien?.let {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
            etDateEntretien.setText(dateFormat.format(it.date_entretien))
            etNotesPreEntretien.setText(it.notes_pre_entretien)
            etNotesPostEntretien.setText(it.notes_post_entretien)
            autoCompleteTextViewEntrepriseEntretien.setText(it.entrepriseNom)
            setupContactAutoCompleteForEntreprise(it.entrepriseNom)
            it.contacts.forEach { contactId ->
                val contact = contactDataRepository.findByCondition { it.id == contactId }.firstOrNull()
                if (contact != null) {
                    multiAutoCompleteTextViewContacts.append("${contact.prenom} ${contact.nom}, ")
                    selectedContacts.add(contact)
                }
            }
            spinnerTypeEntretien.setSelection(resources.getStringArray(R.array.types_entretien).indexOf(it.type))
            spinnerModeEntretien.setSelection(resources.getStringArray(R.array.modes_entretien).indexOf(it.mode))
        }
    }

    private fun setupEntrepriseAutoComplete() {
        val entreprises = entrepriseDataRepository.loadEntreprises().map { it.nom }
        val entrepriseAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises)
        autoCompleteTextViewEntrepriseEntretien.setAdapter(entrepriseAdapter)
    }

    private fun setupContactAutoComplete() {
        autoCompleteTextViewEntrepriseEntretien.setOnItemClickListener { _, _, position, _ ->
            val selectedEntreprise = autoCompleteTextViewEntrepriseEntretien.adapter.getItem(position) as String
            setupContactAutoCompleteForEntreprise(selectedEntreprise)
        }
    }

    private fun setupContactAutoCompleteForEntreprise(entrepriseNom: String) {
        val contacts = contactDataRepository.loadContactsForEntreprise(entrepriseNom)
        val contactNames = contacts.map { "${it.prenom} ${it.nom}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, contactNames)
        multiAutoCompleteTextViewContacts.setAdapter(adapter)
        multiAutoCompleteTextViewContacts.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        multiAutoCompleteTextViewContacts.setOnItemClickListener { _, _, position, _ ->
            val selectedContact = contacts[position]
            if (!selectedContacts.contains(selectedContact)) {
                selectedContacts.add(selectedContact)
            }
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

    private fun saveEntretienChanges() {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
        val dateEntretien = format.parse(etDateEntretien.text.toString()) ?: Date()
        val typeEntretien = spinnerTypeEntretien.selectedItem.toString()
        val modeEntretien = spinnerModeEntretien.selectedItem.toString()
        val notesPreEntretien = etNotesPreEntretien.text.toString()
        val notesPostEntretien = etNotesPostEntretien.text.toString()
        val nomEntreprise = autoCompleteTextViewEntrepriseEntretien.text.toString()

        val entreprise = entrepriseDataRepository.getOrCreateEntreprise(nomEntreprise)

        val updatedEntretien = Entretien(
            id = entretienId ?: UUID.randomUUID().toString(),
            entrepriseNom = entreprise.nom,
            candidatureId = candidatureId ?: "",
            date_entretien = dateEntretien,
            type = typeEntretien,
            mode = modeEntretien,
            notes_pre_entretien = notesPreEntretien,
            notes_post_entretien = notesPostEntretien,
            contacts = selectedContacts.map { it.id }.toMutableList()
        )

        entretienDataRepository.updateOrAddItem(entretienDataRepository.getItems().toMutableList(), updatedEntretien)
        Toast.makeText(this, "Entretien mis à jour avec succès.", Toast.LENGTH_SHORT).show()
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("ENTRETIENS_UPDATED"))
        finish()
    }
}
