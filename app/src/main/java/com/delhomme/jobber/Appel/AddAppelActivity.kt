package com.delhomme.jobber.Appel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.Repository.AppelDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.Entreprise.model.Entreprise
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddAppelActivity : AppCompatActivity() {
    private lateinit var etDateAppel: EditText
    private lateinit var etObjetAppel: EditText
    private lateinit var etNotesAppel: EditText
    private lateinit var spContactsAppel: Spinner
    private lateinit var spEntreprisesAppel: Spinner
    private lateinit var contactMap: Map<String, Contact>
    private lateinit var entrepriseMap: Map<String, Entreprise>

    private lateinit var appelDataRepository: AppelDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_appel)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appelDataRepository = AppelDataRepository(applicationContext)
        contactDataRepository = ContactDataRepository(applicationContext)
        entrepriseDataRepository = EntrepriseDataRepository(applicationContext)

        setupUI()
        setupDatePicker()
        handleIntent()
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
                spContactsAppel.adapter = ArrayAdapter(this@AddAppelActivity, android.R.layout.simple_spinner_dropdown_item, listOf("--"))
            }
        }
    }

    private fun setupSpinners() {
        val entreprises = entrepriseDataRepository.loadEntreprises()
        entrepriseMap = entreprises.associateBy { it.nom }
        val entrepriseNames = entreprises.map { it.nom }
        spEntreprisesAppel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, entrepriseNames)

        spEntreprisesAppel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedEntreprise = entreprises[position]
                updateContactSpinner(selectedEntreprise.nom)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                spContactsAppel.adapter = ArrayAdapter(this@AddAppelActivity, android.R.layout.simple_spinner_dropdown_item, listOf("--"))
            }
        }
    }

    private fun setupDatePicker() {
        etDateAppel.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    TimePickerDialog(this, { _, hour, minute ->
                        val selectedDate = Calendar.getInstance()
                        selectedDate.set(year, month, day, hour, minute)
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
                        etDateAppel.setText(dateFormat.format(selectedDate.time))
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateContactSpinner(entrepriseNom: String) {
        val contacts = contactDataRepository.loadContactsForEntreprise(entrepriseNom)
        contactMap = contacts.associateBy { it.getFullName() }
        val contactNames = contacts.map { it.getFullName() }
        spContactsAppel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contactNames)
    }

    private fun handleIntent() {
        intent.getStringExtra("ENTREPRISE_ID")?.let { entrepriseNom ->
            spEntreprisesAppel.setSelection(entrepriseMap.keys.indexOf(entrepriseNom))
            updateContactSpinner(entrepriseNom)
        }
        // Ancien :
        //val entrepriseNom = intent.getStringExtra("ENTREPRISE_ID")
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")

        /*entrepriseNom?.let {
            val index = entrepriseMap.keys.indexOf(it)
            if (index != -1) {
                spEntreprisesAppel.setSelection(index)
                spEntreprisesAppel.isEnabled = candidatureId == null
                updateContactSpinner(it)
            }
        }*/
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
