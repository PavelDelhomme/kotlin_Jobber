package com.delhomme.jobber.Activity.Relance

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.RelanceDataRepository
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Model.Relance
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddRelanceActivity : AppCompatActivity() {
    private lateinit var etDateRelance: EditText
    private lateinit var spPlateformeRelance: Spinner
    private lateinit var spContact: Spinner
    private lateinit var etNotesRelance: EditText
    private lateinit var autoCompleteTextViewRelanceEntreprise: AutoCompleteTextView
    private lateinit var relanceDataRepository: RelanceDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository // Assurez-vous de l'initialiser correctement
    private lateinit var candidatureDataRepository: CandidatureDataRepository
    private lateinit var contactDataRepository: ContactDataRepository // Assurez-vous de l'initialiser correctement
    private lateinit var contactAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_relance)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        relanceDataRepository = RelanceDataRepository(this)
        candidatureDataRepository = CandidatureDataRepository(this)
        contactDataRepository = ContactDataRepository(this)
        setupUI()
        handleIntentExtras()
        setupListener()
    }

    private fun setupUI() {
        etDateRelance = findViewById(R.id.etDateRelance)
        spPlateformeRelance = findViewById(R.id.spinner_plateforme_relance)
        spContact = findViewById(R.id.spinner_contact_relance)
        etNotesRelance = findViewById(R.id.editText_notes_relance)
        autoCompleteTextViewRelanceEntreprise =
            findViewById(R.id.autoCompleteTextViewRelanceEntreprise)

        ArrayAdapter.createFromResource(
            this,
            R.array.plateforme_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spPlateformeRelance.adapter = adapter
        }
        setupDatePicker()

        findViewById<Button>(R.id.button_add_relance).setOnClickListener { addRelance() }
    }
    private fun setupSpinners() {
        ArrayAdapter.createFromResource(
            this,
            R.array.plateforme_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spPlateformeRelance.adapter = adapter
        }
    }

    private fun handleIntentExtras() {
        val entrepriseId = intent.getStringExtra("ENTREPRISE_ID")
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")

        val entreprise = entrepriseId?.let {
            entrepriseDataRepository.findByCondition { it.nom == entrepriseId }.firstOrNull()
        } ?: candidatureId?.let {
            val candidature = candidatureDataRepository.findByCondition { it.id == candidatureId }.firstOrNull()
            candidature?.let { cand ->
                entrepriseDataRepository.findByCondition { it.nom == cand.entreprise }.firstOrNull()
            }
        }

        entreprise?.let {
            autoCompleteTextViewRelanceEntreprise.setText(it.nom)
            setupContactsSpinner(it.nom)
        }
        autoCompleteTextViewRelanceEntreprise.isEnabled = false
    }


    private fun setupContactsSpinner(entrepriseNom: String) {
        val contacts = contactDataRepository.loadContactsForEntreprise(entrepriseNom)
        val contactNames = contacts.map { it.getFullName() }.toMutableList()
        contactNames.add(0, "Sélectionnez un contact") // Ajoute l'option pour aucun contact sélectionné

        contactAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contactNames)
        contactAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spContact.adapter = contactAdapter
    }

    private fun setupListener() {
        val btnAddRelance = findViewById<Button>(R.id.button_add_relance)
        btnAddRelance.setOnClickListener {
            addRelance()
        }
    }

    private fun setupDatePicker() {
        etDateRelance.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, day, hour, minute)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
                    etDateRelance.setText(dateFormat.format(selectedDate.time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun addRelance() {
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")
        if (candidatureId == null) {
            Toast.makeText(this, "Aucune candidature spécifiée.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).parse(etDateRelance.text.toString()) ?: Date()
        val selectedContactPosition = spContact.selectedItemPosition
        val contact: Contact? = if (selectedContactPosition > 0) {
            spContact.adapter.getItem(selectedContactPosition) as? Contact
        } else {
            null
        }
        val plateforme = spPlateformeRelance.selectedItem.toString()
        val notes = etNotesRelance.text.toString()
        val entrepriseNom = autoCompleteTextViewRelanceEntreprise.text.toString()

        val relance = Relance(
            date_relance = date,
            plateforme_utilisee = plateforme,
            entreprise = entrepriseNom,
            contact = contact?.id,
            candidature = candidatureId,
            notes = notes
        )

        relanceDataRepository.saveItem(relance)
        Toast.makeText(this, "Relance ajoutée avec succès", Toast.LENGTH_SHORT).show()

        val intentEvent = Intent("com.jobber.EVENEMENT_LIST_UPDATED")


        val intentCandidature = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentCandidature)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentEvent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
