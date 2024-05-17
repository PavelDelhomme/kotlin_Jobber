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
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import com.delhomme.jobber.Relance.model.Relance
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

    private lateinit var contactAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_relance)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupUI()
        setupListener()
    }

    private fun setupUI() {
        etDateRelance = findViewById(R.id.etDateRelance)
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

        val entrepriseId = intent.getStringExtra("ENTREPRISE_ID")
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")
        val dr = DataRepository(this)
        val entreprise = entrepriseId?.let { dr.getEntrepriseByNom(it) }
            ?: candidatureId?.let { dr.getCandidatureById(it)?.let { cand -> dr.getEntrepriseByNom(cand.entrepriseNom) } }
        autoCompleteTextViewRelanceEntreprise.setText(entreprise?.nom)
        autoCompleteTextViewRelanceEntreprise.isEnabled = false
    }

    private fun setupContactsSpinner() {
        val dr = DataRepository(this)
        val entrepriseId = intent.getStringExtra("ENTREPRISE_ID")
        val contacts = entrepriseId?.let { dr.loadContactsForEntreprise(it) } ?: listOf()
        contactAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contacts.map { it.getFullName() })
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
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
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

        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(etDateRelance.text.toString()) ?: Date()
        val selectedContactPosition = spContact.selectedItemPosition
        val contact = (spContact.adapter.getItem(selectedContactPosition) as? Contact)
        val plateforme = spPlateformeRelance.selectedItem.toString()
        val notes = etNotesRelance.text.toString()
        val entrepriseNom = autoCompleteTextViewRelanceEntreprise.text.toString()

        val relance = Relance(
            date_relance = date,
            plateformeUtilisee = plateforme,
            entrepriseNom = entrepriseNom,
            contactId = contact?.id,
            candidatureId = candidatureId,
            notes = notes
        )

        DataRepository(this).saveRelance(relance)
        Toast.makeText(this, "Relance ajoutée avec succès", Toast.LENGTH_SHORT).show()
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
