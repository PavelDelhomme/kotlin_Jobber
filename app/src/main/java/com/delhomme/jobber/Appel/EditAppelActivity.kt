package com.delhomme.jobber.Appel

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
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditAppelActivity : AppCompatActivity() {
    private lateinit var etDateAppel: EditText
    private lateinit var etObjetAppel: EditText
    private lateinit var etNotesAppel: EditText
    private lateinit var spContact: Spinner
    private lateinit var autoCompleteTextViewEntreprise: AutoCompleteTextView
    private lateinit var dataRepository: DataRepository
    private var appelId: String? = null
    private var entrepriseId: String? = null
    private var contactId: String? = null
    private var candidatureId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appel)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataRepository = DataRepository(this)
        appelId = intent.getStringExtra("APPEL_ID")
        candidatureId = intent.getStringExtra("CANDIDATURE_ID")
        entrepriseId = intent.getStringExtra("ENTREPRISE_ID")
        contactId = intent.getStringExtra("CONTACT_ID")

        setupUI()
        loadData()
    }

    private fun setupUI() {
        etDateAppel = findViewById(R.id.editTextDateAppelEdit)
        etObjetAppel = findViewById(R.id.editTextObjetAppelEdit)
        etNotesAppel = findViewById(R.id.editTextNotesAppelEdit)
        spContact = findViewById(R.id.spinnerContactAppel)
        autoCompleteTextViewEntreprise = findViewById(R.id.autoCompleteTextViewEntrepriseAppel)

        setupDatePicker()
        setupContactSpinner()
        setupEntrepriseField()

        findViewById<Button>(R.id.btnSaveAppelEdit).setOnClickListener {
            saveAppelChanges()
        }
    }

    private fun setupDatePicker() {
        etDateAppel.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                etDateAppel.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time))
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupContactSpinner() {
        val contacts = entrepriseId?.let { dataRepository.loadContactsForEntreprise(it) } ?: dataRepository.loadContacts()
        val contactAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contacts.map { it.getFullName() })
        spContact.adapter = contactAdapter
        contactId?.let {
            val position = contacts.indexOfFirst { it.id == contactId }
            spContact.setSelection(if (position != -1) position else 0)
        }
    }

    private fun setupEntrepriseField() {
        if (entrepriseId != null) {
            val entreprise = dataRepository.getEntrepriseById(entrepriseId)
            autoCompleteTextViewEntreprise.setText(entreprise?.nom)
        } else {
            autoCompleteTextViewEntreprise.isEnabled = true
        }
    }

    private fun loadData() {
        appelId?.let {
            val appel = dataRepository.getAppelById(it)
            appel?.let {
                etDateAppel.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(appel.date_appel))
                etObjetAppel.setText(appel.objet)
                etNotesAppel.setText(appel.notes)
            }
        }
    }

    private fun saveAppelChanges() {
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(etDateAppel.text.toString()) ?: Date()
        val objet = etObjetAppel.text.toString()
        val notes = etNotesAppel.text.toString()
        val contact = spContact.selectedItem as Contact

        appelId?.let {
            val updatedAppel = Appel(
                it,
                candidature_id = candidatureId,
                contact_id = contact.id,
                entreprise_id = contact.entrepriseId,
                date_appel = date,
                objet = objet,
                notes = notes
            )
            dataRepository.saveAppel(updatedAppel)
            Toast.makeText(this, "Appel mis à jour avec succès.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
