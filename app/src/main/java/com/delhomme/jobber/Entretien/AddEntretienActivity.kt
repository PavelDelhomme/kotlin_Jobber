package com.delhomme.jobber.Entretien

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
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.Entretien.model.Entretien
import com.delhomme.jobber.R
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
    private lateinit var autoCompleteTextViewContact: AutoCompleteTextView
    private lateinit var dataRepository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entretien)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataRepository = DataRepository(this)
        setupUI()
        setupListeners()
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

        setupDateTimePicker()
        setupEntrepriseAutoComplete()
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.button_add_entretien).setOnClickListener {
            addEntretien()
        }
    }

    private fun setupDateTimePicker() {
        etDateEntretien.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, day, hour, minute)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    etDateEntretien.setText(dateFormat.format(selectedDate.time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupEntrepriseAutoComplete() {
        val entreprises = dataRepository.getEntreprises()
        val entrepriseAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises.map { it.nom })
        autoCompleteTextViewEntreprise.setAdapter(entrepriseAdapter)

        val entrepriseId = intent.getStringExtra("ENTREPRISE_ID")
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")

        if (entrepriseId != null || candidatureId != null) {
            val entreprise = entrepriseId?.let { dataRepository.getEntrepriseByNom(it) }
                ?: candidatureId?.let { dataRepository.getCandidatureById(it)?.let { cand -> dataRepository.getEntrepriseByNom(cand.entrepriseNom) } }

            autoCompleteTextViewEntreprise.setText(entreprise?.nom)
            autoCompleteTextViewEntreprise.isEnabled = false
            setupContactAutoComplete(entreprise?.nom)
        }

        autoCompleteTextViewEntreprise.setOnItemClickListener { parent, _, position, _ ->
            val selectedEntreprise = parent.getItemAtPosition(position) as String
            setupContactAutoComplete(selectedEntreprise)
        }
    }

    private fun setupContactAutoComplete(entrepriseNom: String?) {
        if (entrepriseNom.isNullOrEmpty()) return
        val contacts = dataRepository.loadContactsForEntreprise(entrepriseNom)
        val contactAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, contacts.map { "${it.nom} ${it.prenom}" })
        autoCompleteTextViewContact.setAdapter(contactAdapter)
    }

    private fun addEntretien() {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateEntretien = format.parse(etDateEntretien.text.toString()) ?: Date()
        val typeEntretien = spinnerTypeEntretien.selectedItem.toString()
        val modeEntretien = spinnerModeEntretien.selectedItem.toString()
        val notesPreEntretien = etNotesPreEntretien.text.toString()
        val nomEntreprise = autoCompleteTextViewEntreprise.text.toString()
        val nomContact = autoCompleteTextViewContact.text.toString()

        val (nom, prenom) = nomContact.split(" ").let {
            if (it.size == 2) it[0] to it[1] else return@let "Unknown" to "Contact"
        }

        val entreprise = dataRepository.getOrCreateEntreprise(nomEntreprise)
        val contact = dataRepository.getOrCreateContact(nom, prenom, entreprise.nom)

        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")

        candidatureId?.let {
            dataRepository.addCandidatureToContact(contact.id, it)
        }

        val entretien = Entretien(
            id = UUID.randomUUID().toString(),
            entrepriseNom = entreprise.nom,
            contact_id = contact.id,
            candidature_id = candidatureId ?: "",
            date_entretien = dateEntretien,
            type = typeEntretien,
            mode = modeEntretien,
            notes_pre_entretien = notesPreEntretien,
        )

        dataRepository.saveEntretien(entretien)
        entreprise.entretiens.add(entretien.id)
        dataRepository.saveEntreprise(entreprise)

        Toast.makeText(this, "Entretien ajouté pour ${entreprise.nom}", Toast.LENGTH_SHORT).show()
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("ENTRETIENS_UPDATED"))
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
