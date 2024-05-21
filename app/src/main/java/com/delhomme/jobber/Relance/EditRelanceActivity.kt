package com.delhomme.jobber.Relance

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
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditRelanceActivity : AppCompatActivity() {
    private lateinit var dataRepository: DataRepository
    private var relanceId: String? = null
    private lateinit var autoCompleteEntreprise: AutoCompleteTextView
    private lateinit var etDateRelance: EditText
    private lateinit var spPlateformeRelance: Spinner
    private lateinit var spContact: Spinner
    private lateinit var etNotesRelance: EditText
    private lateinit var candidatureId: String
    private lateinit var entrepriseId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_relance)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataRepository = DataRepository(applicationContext)
        relanceId = intent.getStringExtra("RELANCE_ID")
        entrepriseId = intent.getStringExtra("ENTREPRISE_ID") ?: ""
        candidatureId = intent.getStringExtra("CANDIDATURE_ID") ?: ""

        setupEntrepriseAutoComplete()
        setupUI()
        setupListeners()
        loadData()

        findViewById<Button>(R.id.btnCancelRelanceChanges).setOnClickListener {
            cancelChanges()
        }
    }

    private fun setupEntrepriseAutoComplete() {
        val entreprises = dataRepository.getEntreprises().map { it.nom }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises)
        autoCompleteEntreprise = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewRelanceEntreprise).apply {
            setAdapter(adapter)
            setText(dataRepository.getEntrepriseByNom(entrepriseId)?.nom)
            threshold = 1
        }
    }

    private fun setupUI() {
        etDateRelance = findViewById(R.id.etDateRelance)
        spPlateformeRelance = findViewById(R.id.spinner_plateforme_relance)
        spContact = findViewById(R.id.spinner_contact_relance)
        etNotesRelance = findViewById(R.id.editText_notes_relance)

        ArrayAdapter.createFromResource(
            this,
            R.array.plateforme_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spPlateformeRelance.adapter = adapter
        }

        setupDatePicker()
        setupContactsSpinner()
    }

    private fun setupListeners() {
        val btnSaveRelance = findViewById<Button>(R.id.button_save_relance_changes)
        btnSaveRelance.setOnClickListener {
            saveChanges()
        }
    }

    private fun setupDatePicker() {
        etDateRelance.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, day, hour, minute)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    etDateRelance.setText(dateFormat.format(selectedDate.time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupContactsSpinner() {
        val contacts = dataRepository.getContacts()
        val contactAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contacts.map { it.getFullName() })
        contactAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spContact.adapter = contactAdapter
    }

    private fun loadData() {
        relanceId?.let {
            val relance = dataRepository.getRelanceById(it)
            relance?.let {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                etDateRelance.setText(dateFormat.format(relance.date_relance))
                etNotesRelance.setText(relance.notes)
                spPlateformeRelance.setSelection((spPlateformeRelance.adapter as ArrayAdapter<String>).getPosition(relance.plateformeUtilisee))
                val contactIndex = (spContact.adapter as ArrayAdapter<String>).getPosition(dataRepository.getContactById(relance.contactId)?.getFullName())
                spContact.setSelection(maxOf(0, contactIndex))
            }
        }
    }

    private fun saveChanges() {
        val selectedContactName = spContact.selectedItem.toString()
        val contact = dataRepository.getContacts().find { it.getFullName() == selectedContactName }
        val contactId = contact?.id
        val dateRelance = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(etDateRelance.text.toString())!!
        val plateforme = spPlateformeRelance.selectedItem.toString()
        val notes = etNotesRelance.text.toString()

        if (relanceId != null) {
            dataRepository.editRelance(
                relanceId!!,
                dateRelance,
                plateforme,
                entrepriseId,
                contactId,
                candidatureId,
                notes
            )
            Toast.makeText(this, "Relance mise à jour.", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Erreur : ID de relance manquant", Toast.LENGTH_SHORT).show()
        }
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

    private fun cancelChanges() {
        finish()
    }
}
