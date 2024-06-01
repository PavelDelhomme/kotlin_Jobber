package com.delhomme.jobber.Activity.Appel

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
import com.delhomme.jobber.Api.Repository.AppelDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Model.Appel
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

    private lateinit var appelDataRepository: AppelDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository

    private var appelId: String? = null
    private var entrepriseNom: String? = null
    private var contactId: String? = null
    private var candidatureId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appel)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appelDataRepository = AppelDataRepository(this)
        contactDataRepository = ContactDataRepository(this)
        entrepriseDataRepository = EntrepriseDataRepository(this)

        appelId = intent.getStringExtra("APPEL_ID")
        candidatureId = intent.getStringExtra("CANDIDATURE_ID")
        entrepriseNom = intent.getStringExtra("ENTREPRISE_ID")
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
        findViewById<Button>(R.id.btnCancelAppelChanges).setOnClickListener {
            cancelChanges()
        }
    }

    private fun setupDatePicker() {
        etDateAppel.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, day, hour, minute)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
                    etDateAppel.setText(dateFormat.format(selectedDate.time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupContactSpinner() {
        val contacts = entrepriseNom?.let {
            entrepriseDataRepository.findByCondition { it.nom == entrepriseNom }.flatMap {
                contactDataRepository.findByCondition { contact -> contact.entreprise == it.nom }
            }
        } ?: contactDataRepository.getItems()
        val contactNames = contacts.map { it.getFullName() }.toMutableList()
        contactNames.add(0, "--") // Option par défaut pour aucun contact sélectionné

        val contactAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contactNames)
        contactAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spContact.adapter = contactAdapter
        contactId?.let {
            val position = contacts.indexOfFirst { it.id == contactId }
            spContact.setSelection(if (position != -1) position + 1 else 0)
        }
    }

    private fun setupEntrepriseField() {
        if (entrepriseNom != null) {
            val entreprise = entrepriseDataRepository.findByCondition { it.nom == entrepriseNom }.firstOrNull()
            autoCompleteTextViewEntreprise.setText(entreprise?.nom)
        } else {
            autoCompleteTextViewEntreprise.isEnabled = true
        }
    }

    private fun loadData() {
        appelId?.let {
            val appel = appelDataRepository.findByCondition { it.id == appelId }.firstOrNull()
            appel?.let {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
                etDateAppel.setText(dateFormat.format(appel.date_appel))
                etObjetAppel.setText(appel.objet)
                etNotesAppel.setText(appel.notes)
            }
        }
    }

    private fun saveAppelChanges() {
        try {
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).parse(etDateAppel.text.toString()) ?: Date()
            val objet = etObjetAppel.text.toString()
            val notes = etNotesAppel.text.toString()
            val selectedContactName = spContact.selectedItem.toString()
            val selectedContctId = if (selectedContactName != "--") contactDataRepository.findByCondition { it.getFullName() == selectedContactName }.firstOrNull()?.id else null

            appelId?.let {
                val updatedAppel = Appel(
                    it,
                    candidature = candidatureId,
                    contact = selectedContctId,
                    entrepriseNom = entrepriseNom,
                    date_appel = date,
                    objet = objet,
                    notes = notes
                )
                appelDataRepository.saveItem(updatedAppel)
                Toast.makeText(this, "Appel mis à jour avec succès.", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erreur lors de la mise à jour de l'appel: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cancelChanges() {
        finish()
    }

}
