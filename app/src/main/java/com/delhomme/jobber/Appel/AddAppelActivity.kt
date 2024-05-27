package com.delhomme.jobber.Appel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.Model.Entreprise
import com.delhomme.jobber.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddAppelActivity : AppCompatActivity() {
    private lateinit var etDateAppel: EditText
    private lateinit var etObjetAppel: EditText
    private lateinit var etNotesAppel: EditText
    private lateinit var spContactsAppel: Spinner
    private lateinit var spEntreprisesAppel: Spinner
    private lateinit var contactMap: Map<String, Contact>
    private lateinit var entrepriseMap: Map<String, Entreprise>

    private lateinit var dataRepository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_appel)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        dataRepository = DataRepository(applicationContext)
        setupUI()
        setupDatePicker()
        handleIntent()

        findViewById<Button>(R.id.btnSaveAppel).setOnClickListener {
            addAppel(it)
        }
    }

    private fun setupUI() {
        etDateAppel = findViewById(R.id.etDateAppel)
        etObjetAppel = findViewById(R.id.etObjetAppel)
        etNotesAppel = findViewById(R.id.etNotesAppel)
        spContactsAppel = findViewById(R.id.spContactsAppel)
        spEntreprisesAppel = findViewById(R.id.spEntreprisesAppel)

        spEntreprisesAppel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, nom: Long) {
                val selectedEntreprise = parent.getItemAtPosition(position) as Entreprise
                updateContactSpinner(selectedEntreprise.nom)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                spContactsAppel.adapter = ArrayAdapter<String>(this@AddAppelActivity, android.R.layout.simple_spinner_dropdown_item, listOf("--"))
            }
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

    private fun handleIntent() {
        val entrepriseNom = intent.getStringExtra("ENTREPRISE_ID")
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")

        val entreprises = dataRepository.getEntreprises()
        entrepriseMap = entreprises.associateBy { it.nom }
        val entrepriseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, entreprises)
        spEntreprisesAppel.adapter = entrepriseAdapter

        if (entrepriseNom != null) {
            val index = entreprises.indexOfFirst { it.nom == entrepriseNom }
            if (index != -1) {
                spEntreprisesAppel.setSelection(index)
                spEntreprisesAppel.isEnabled = candidatureId == null
            }
            updateContactSpinner(entrepriseNom)
        } else {
            updateContactSpinner(null)
        }
    }

    private fun updateContactSpinner(entrepriseNom: String?) {
        val contacts = dataRepository.loadContactsForEntreprise(entrepriseNom ?: "")
        val contactNames = contacts.map { it.getFullName() }.toMutableList().apply {
            add(0, "--")
        }
        spContactsAppel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contactNames)
        contactMap = contacts.associateBy { it.getFullName() }
    }


    private fun addAppel(view: View) {
        val selectedContactName = spContactsAppel.selectedItem.toString()
        val contactId = if (selectedContactName != "--") contactMap[selectedContactName]?.id else null
        val selectedEntreprise = spEntreprisesAppel.selectedItem as? Entreprise
        val entrepriseNom = selectedEntreprise?.nom ?: "No Company"
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")

        if (entrepriseNom == "No Company") {
            Toast.makeText(this, "Erreur: entreprise non sélectionnée ou non trouvée.", Toast.LENGTH_LONG).show()
            return
        }
        try {
            val dateAppel = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).parse(etDateAppel.text.toString()) ?: Date()
            val objet = etObjetAppel.text.toString()
            val notes = etNotesAppel.text.toString()
            val appel = Appel(contact_id = contactId, entrepriseNom = entrepriseNom, date_appel = dateAppel, objet = objet, notes = notes, candidature_id = candidatureId)
            dataRepository.saveAppel(appel)

            candidatureId?.let {
                val candidature = dataRepository.getCandidatureById(it)
                candidature?.appels?.add(appel.id)
                dataRepository.saveCandidature(candidature!!)
            }

            contactId?.let {
                val contact = dataRepository.getContactById(it)
                contact?.appelsIds?.add(appel.id)
                dataRepository.saveContact(contact!!)
            }

            finish()
        } catch (e: ParseException) {
            Toast.makeText(this, "Format de date invalide", Toast.LENGTH_SHORT).show()
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
