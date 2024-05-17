package com.delhomme.jobber.Relance

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
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditRelanceActivity : AppCompatActivity() {
    private lateinit var dataRepository: DataRepository
    private lateinit var autoCompleteEntreprise: AutoCompleteTextView
    private var relanceId: String? = null
    private lateinit var candidatureId: String
    private lateinit var entrepriseId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_relance)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        dataRepository = DataRepository(applicationContext)
        relanceId = intent.getStringExtra("RELANCE_ID")

        candidatureId = intent.getStringExtra("CANDIDATURE_ID").toString()

        setupEntrepriseAutoComplete()

        val candidature = candidatureId?.let { dataRepository.getCandidatureById(it) }

        if (candidature != null) {
            setupUI()
        }


        entrepriseId = intent.getStringExtra("ENTREPRISE_ID").toString()
        setupListeners()
        loadData()

        findViewById<Button>(R.id.btnSaveRelance)
    }

    private fun setupEntrepriseAutoComplete() {
        val entreprises = dataRepository.getEntreprises().map { it.nom }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises)
        findViewById<AutoCompleteTextView>(R.id.actvNomEntrepriseRelance).apply {
            setAdapter(adapter)
        }
    }

    private fun setupFields() {
        relanceId?.let {
            val relance = dataRepository.getRelanceById(it)
            relance?.let { rel ->
                findViewById<EditText>(R.id.)
            }
        }
    }

    private fun setupUI() {
        etDateRelance = findViewById(R.id.editText_date_relance)
        spPlateformeRelance = findViewById(R.id.spinner_plateforme_relance)
        spContact = findViewById(R.id.spinner_contact_relance)
        etNotesRelance = findViewById(R.id.editText_notes_relance)
        autoCompleteTextViewRelanceEntreprise = findViewById(R.id.autoCompleteTextViewRelanceEntreprise)

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
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                etDateRelance.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time))
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupContactsSpinner() {
        val contacts = dataRepository.loadContacts()
        val contactAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contacts.map { it.getFullName() })
        contactAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spContact.adapter = contactAdapter
    }

    private fun loadData() {
        relanceId?.let {
            val relance = dataRepository.getRelanceById(it)
            relance?.let {
                etDateRelance.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(relance.date_relance))
                etNotesRelance.setText(relance.notes)
                spPlateformeRelance.setSelection((spPlateformeRelance.adapter as ArrayAdapter<String>).getPosition(relance.plateformeUtilisee))
                val contactIndex = (spContact.adapter as ArrayAdapter<String>).getPosition(dataRepository.getContactById(relance.contactId)?.getFullName())
                spContact.setSelection(maxOf(0, contactIndex)) // To handle -1 if not found
            }
        }
    }

    private fun saveChanges() {
        val contactId = (spContact.selectedItem as Contact).id
        val date_relance = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(findViewById<EditText>(R.id.etDateRelance).text.toString())!!
        val plateforme = findViewById<Spinner>(R.id.spinner_plateforme).selectedItem.toString()
        val notes = findViewById<EditText>(R.id.notesRelance).text.toString()

        if (relanceId != null) {
            dataRepository.editRelance(
                relanceId!!,
                date_relance,
                plateforme,
                entrepriseId,
                contactId,
                candidatureId,
                notes
            )
            Toast.makeText(this, "Relance mise à jours.", Toast.LENGTH_LONG).show()
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
}