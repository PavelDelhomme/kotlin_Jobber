package com.delhomme.jobber.Activity.Relance

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
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.RelanceDataRepository
import com.delhomme.jobber.Model.Relance
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditRelanceActivity : AppCompatActivity() {
    private lateinit var relanceDataRepository: RelanceDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var etDateRelance: EditText
    private lateinit var spPlateformeRelance: Spinner
    private lateinit var spContact: Spinner
    private lateinit var etNotesRelance: EditText
    private lateinit var autoCompleteEntreprise: AutoCompleteTextView
    private var relanceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_relance)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialisez les instances de vos repositories
        relanceDataRepository = RelanceDataRepository(this)
        entrepriseDataRepository = EntrepriseDataRepository(this)
        contactDataRepository = ContactDataRepository(this)

        setupUI()
        setupListeners()
        loadData()
    }


    private fun setupUI() {
        etDateRelance = findViewById(R.id.etDateRelance)
        spPlateformeRelance = findViewById(R.id.spinner_plateforme_relance)
        spContact = findViewById(R.id.spinner_contact_relance)
        etNotesRelance = findViewById(R.id.editText_notes_relance)
        autoCompleteEntreprise = findViewById(R.id.autoCompleteTextViewRelanceEntreprise)

        setupSpinners()
        setupDatePicker()
        setupContactsSpinner()
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.btnSaveRelanceChanges).setOnClickListener {
            saveChanges()
        }
        findViewById<Button>(R.id.btnCancelRelanceChanges).setOnClickListener {
            cancelChanges()
        }
    }

    private fun setupSpinners() {
        val plateformeOptions = resources.getStringArray(R.array.plateforme_options)
        val plateformeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, plateformeOptions)
        plateformeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPlateformeRelance.adapter = plateformeAdapter
    }

    private fun setupContactsSpinner() {
        val contacts = contactDataRepository.getItems()
        val contactNames = contacts.map { it.getFullName() }
        val contactAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contactNames)
        contactAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spContact.adapter = contactAdapter
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
    private fun loadData() {
        relanceId?.let { id ->
            val relance = relanceDataRepository.findByCondition { relance -> relance.id == id }.firstOrNull()
            relance?.let { r ->
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
                etDateRelance.setText(dateFormat.format(r.date_relance))
                etNotesRelance.setText(r.notes)
                spPlateformeRelance.setSelection((spPlateformeRelance.adapter as ArrayAdapter<String>).getPosition(r.plateforme_utilisee))
                val contactIndex = (spContact.adapter as ArrayAdapter<String>).getPosition(contactDataRepository.findByCondition { contact -> contact.id == r.contact }.firstOrNull()?.getFullName() ?: "")
                spContact.setSelection(maxOf(0, contactIndex))
            }
        }
    }


    private fun saveChanges() {
        val dateRelance = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).parse(etDateRelance.text.toString())!!
        val plateforme = spPlateformeRelance.selectedItem.toString()
        val notes = etNotesRelance.text.toString()
        val contact = contactDataRepository.findByCondition { it.getFullName() == spContact.selectedItem.toString() }.firstOrNull()

        relanceId?.let {
            val updatedRelance = Relance(
                id = it,
                date_relance = dateRelance,
                plateforme_utilisee = plateforme,
                entreprise = autoCompleteEntreprise.text.toString(),
                contact = contact?.id,
                candidature = intent.getStringExtra("CANDIDATURE_ID").toString(), // Adjust as necessary
                notes = notes
            )
            relanceDataRepository.updateOrAddItem(relanceDataRepository.getItems().toMutableList(), updatedRelance)
            Toast.makeText(this, "Relance mise à jour.", Toast.LENGTH_LONG).show()
            finish()
        } ?: Toast.makeText(this, "Erreur : ID de relance manquant", Toast.LENGTH_SHORT).show()
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
