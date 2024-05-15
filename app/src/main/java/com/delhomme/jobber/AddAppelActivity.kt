package com.delhomme.jobber

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.models.Appel
import com.delhomme.jobber.models.Contact
import com.delhomme.jobber.models.Entreprise
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_appel)
        setupActionBar()
        setupUI()
        setupDatePicker()
        handleIntent()

        findViewById<Button>(R.id.btnSaveAppel).setOnClickListener {
            addAppel(it)
        }
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupUI() {
        etDateAppel = findViewById(R.id.etDateAppel)
        etObjetAppel = findViewById(R.id.etObjetAppel)
        etNotesAppel = findViewById(R.id.etNotesAppel)
        spContactsAppel = findViewById(R.id.spContactsAppel)
        spEntreprisesAppel = findViewById(R.id.spEntreprisesAppel)

        spEntreprisesAppel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedEntreprise = parent.getItemAtPosition(position) as Entreprise
                updateContactSpinner(selectedEntreprise.id)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionnellement, nettoyez le spinner des contacts si rien n'est sélectionné
                spContactsAppel.adapter = ArrayAdapter<String>(this@AddAppelActivity, android.R.layout.simple_spinner_dropdown_item, listOf())
            }
        }
    }

    private fun updateContactSpinner(entrepriseId: String) {
        val dataRepository = DataRepository(this)
        val contacts = dataRepository.loadContactsForEntreprise(entrepriseId)
        val contactsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contacts.map { it.getFullName() })
        spContactsAppel.adapter = contactsAdapter
    }
    private fun setupDatePicker() {
        etDateAppel.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                etDateAppel.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time))
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
    }

    private fun handleIntent() {
        val dataRepository = DataRepository(this)
        val entrepriseId = intent.getStringExtra("ENTREPRISE_ID")

        val entreprises: List<Entreprise> = if (entrepriseId != null) {
            // Charge uniquement l'entreprise spécifiée
            listOfNotNull(dataRepository.getEntrepriseById(entrepriseId))
        } else {
            // Charge toutes les entreprises disponibles
            dataRepository.loadEntreprises()
        }

        // Charge les contacts de l'entreprise spécifiée ou tous les contacts si aucune entreprise n'est spécifiée
        val contacts = entrepriseId?.let {
            dataRepository.loadContactsForEntreprise(it)
        } ?: dataRepository.loadContacts()

        configureSpinners(contacts, entreprises)
    }

    private fun configureSpinners(contacts: List<Contact>, entreprises: List<Entreprise>) {
        val contactAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contacts.map { it.getFullName() })
        spContactsAppel.adapter = contactAdapter

        val entrepriseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, entreprises)
        spEntreprisesAppel.adapter = entrepriseAdapter
    }

    private fun addAppel(view: View) {
        if (::contactMap.isInitialized && ::entrepriseMap.isInitialized) {
            val dateAppel = etDateAppel.text.toString()
            val objet = etObjetAppel.text.toString()
            val notes = etNotesAppel.text.toString()

            val selectedContactName = spContactsAppel.selectedItem.toString()
            val selectedEntrepriseName = spEntreprisesAppel.selectedItem.toString()

            val contact = contactMap[selectedContactName]
            val entreprise = entrepriseMap[selectedEntrepriseName]

            if (contact != null && entreprise != null) {
                val appel = Appel(
                    contact_id = contact.id,
                    entreprise_id = entreprise.id,
                    date_appel = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateAppel) ?: Date(),
                    objet = objet,
                    notes = notes
                )
                DataRepository(applicationContext).saveAppel(appel)
                finish()
            } else {
                Log.d("AddAppelActivity", "Contact : ${contact}")
                Log.d("AddAppelActivity", "ContactMap : ${contactMap}")
                Log.d("AddAppelActivity", "selectedContactName : ${selectedContactName}")
                Log.d("AddAppelActivity", "Entreprise : ${entreprise}")
                Log.d("AddAppelActivity", "EntrepriseMap : ${entrepriseMap}")
                Log.d("AddAppelActivity", "selectedEntrepriseName : ${selectedEntrepriseName}")
                Log.e("AddAppelActivity", "Contact or enterprise not found.")
            }
        } else {
            Log.e("AddAppelActivity", "Contact map or entreprise map not initialized")
        }
    }

}