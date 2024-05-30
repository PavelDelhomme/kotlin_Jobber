package com.delhomme.jobber.Activity.Entretien

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Model.Entreprise
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddEntretienActivity : AppCompatActivity() {
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var entretienDataRepository: EntretienDataRepository
    private lateinit var candidatureDataRepository: CandidatureDataRepository

    private lateinit var etDateEntretien: EditText
    private lateinit var etNotesPreEntretien: EditText
    private lateinit var spinnerTypeEntretien: Spinner
    private lateinit var spinnerModeEntretien: Spinner
    private lateinit var autoCompleteTextViewEntreprise: AutoCompleteTextView
    private lateinit var autoCompleteTextViewContact: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entretien)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRepositories()
        setupUI()
    }

    private fun initRepositories() {
        entrepriseDataRepository = EntrepriseDataRepository(this)
        contactDataRepository = ContactDataRepository(this)
        entretienDataRepository = EntretienDataRepository(this)
        candidatureDataRepository = CandidatureDataRepository(this)
    }

    private fun setupUI() {
        etDateEntretien = findViewById(R.id.dateEntretien)
        etNotesPreEntretien = findViewById(R.id.etNotesEntretien)
        spinnerTypeEntretien = findViewById(R.id.spinner_type_entretien)
        spinnerModeEntretien = findViewById(R.id.spinner_mode_entretien)
        autoCompleteTextViewEntreprise = findViewById(R.id.autoCompleteTextViewEntretien)
        autoCompleteTextViewContact = findViewById(R.id.autoCompleteTextViewContact)

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
        /*
        findViewById<Button>(R.id.btnAddNewContact).setOnClickListener {
            toggleNewContactFields()
        }*/


        setupEntrepriseAutoComplete()
        setupContactAutoComplete()
        /*setupNewContactFields()*/
        setupDateTimePicker()
        setupListeners()
    }

    private fun setupEntrepriseAutoComplete() {
        val entreprises = entrepriseDataRepository.loadEntreprises().map { it.nom }
        val entrepriseAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises)
        autoCompleteTextViewEntreprise.setAdapter(entrepriseAdapter)
    }

    private fun setupContactAutoComplete() {
        val contacts = contactDataRepository.loadItems().map { it.getFullName() }
        val contactAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, contacts)
        autoCompleteTextViewContact.setAdapter(contactAdapter)
    }
    /*
    private fun toggleNewContactFields() {
        val visibility = if (findViewById<EditText>(R.id.newContactFirstName).visibility == View.GONE) View.VISIBLE else View.GONE
        findViewById<EditText>(R.id.newContactFirstName).visibility = visibility
        findViewById<EditText>(R.id.newContactLastName).visibility = visibility
        //findViewById<EditText>(R.id.newContactEmail).visibility = visibility
        //findViewById<EditText>(R.id.newContactPhone).visibility = visibility
    }*/

    /*
    private fun showContactPicker() {
        val contacts = contactDataRepository.loadContactsForEntreprise(autoCompleteTextViewEntreprise.text.toString())
        val contactNames = contacts.map { "${it.prenom} ${it.nom}" }.toTypedArray()
        val selectedContacts = BooleanArray(contactNames.size)
        AlertDialog.Builder(this)
            .setTitle("Sélectionnez les contacts")
            .setMultiChoiceItems(contactNames, selectedContacts) { _, which, isChecked ->
                selectedContacts[which] = isChecked
            }
            .setPositiveButton("OK") { dialog, _ ->
                val selectedContactIds = contacts.filterIndexed { index, _ -> selectedContacts[index] }.map { it.id }
                dialog.dismiss()
            }
            .setNegativeButton("Annuler") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }*/

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
        findViewById<Button>(R.id.button_add_entretien).setOnClickListener {
            addEntretien()
        }
    }
    private fun addEntretien() {
        val nomEntreprise = autoCompleteTextViewEntreprise.text.toString()
        val parts = autoCompleteTextViewContact.text.toString().split(" ")
        if (parts.size < 2) {
            Toast.makeText(this, "Veuillez entrer un nom et prénom valide pour le contact", Toast.LENGTH_SHORT).show()
            return
        }
        val contact = contactDataRepository.getOrCreateContact(parts[0], parts.drop(1).joinToString(" "), nomEntreprise)
        val entreprise = entrepriseDataRepository.getOrCreateEntreprise(nomEntreprise)
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID") ?: ""

        val entretien = createEntretien(contact, entreprise, candidatureId)
    }


    private fun createEntretien(contact: Contact, entreprise: Entreprise, candidatureId: String) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
        val dateEntretien = dateFormat.parse(etDateEntretien.text.toString()) ?: Date()
        val typeEntretien = spinnerTypeEntretien.selectedItem.toString()
        val modeEntretien = spinnerModeEntretien.selectedItem.toString()
        val notesPreEntretien = etNotesPreEntretien.text.toString()
        val entretien = Entretien(
            id = UUID.randomUUID().toString(),
            date_entretien = dateEntretien,
            type = typeEntretien,
            mode = modeEntretien,
            notes_pre_entretien = notesPreEntretien,
            entrepriseNom = entreprise.nom,
            contact_id = contact.id,
            candidature_id = candidatureId ?: "",
            contact = contact
        )

        entretienDataRepository.updateOrAddItem(entretienDataRepository.getItems().toMutableList(), entretien)
        entreprise.entretiens.add(entretien.id)
        entrepriseDataRepository.saveItem(entreprise)

        candidatureId?.let {
            // Associer cet entretien à la candidature si spécifié
            candidatureDataRepository.addEntretienToCandidature(it, entretien.id)
        }

        Toast.makeText(this, "Entretien ajouté avec succès.", Toast.LENGTH_SHORT).show()
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
