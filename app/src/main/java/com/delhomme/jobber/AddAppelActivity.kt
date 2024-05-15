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
import android.widget.Toast
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
                spContactsAppel.adapter = ArrayAdapter<String>(this@AddAppelActivity, android.R.layout.simple_spinner_dropdown_item, listOf("--"))
            }
        }
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

        // Charger les entreprises
        val entreprises = dataRepository.loadEntreprises()
        //entrepriseMap = entreprises.associateBy { it.nom }
        entrepriseMap = entreprises.associateBy { it.id }

        // Configurer le spinner des entreprises
        val entrepriseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, entreprises)
        spEntreprisesAppel.adapter = entrepriseAdapter

        // Gérer la sélection initiale et la possibilité de changer d'entreprise
        if (entrepriseId != null) {
            val index = entreprises.indexOfFirst { it.id == entrepriseId }
            if (index != -1) {
                spEntreprisesAppel.setSelection(index)
                spEntreprisesAppel.isEnabled = false
                updateContactSpinner(entrepriseId)
            }
        }
        // Charger les contacts selon l'entreprise, si spécifiée
        val contacts = entrepriseId?.let { dataRepository.loadContactsForEntreprise(it) } ?: listOf()
        contactMap = contacts.associateBy { it.id }
        // Mettre à jour le spinner des contacts
        updateContactSpinner(null) // Pour initialiser avec "--"
    }
    private fun updateContactSpinner(entrepriseId: String?) {
        val dataRepository = DataRepository(this)
        val contacts = dataRepository.loadContactsForEntreprise(entrepriseId)
        val contactNames = listOf("--") + contacts.map { it.getFullName() }
        spContactsAppel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contactNames)
    }

    private fun addAppel(view: View) {
        // S'assurer que les maps sont initialisées
        if (!::contactMap.isInitialized || !::entrepriseMap.isInitialized) {
            Toast.makeText(this, "Les données sont en cours de chargement, veuillez patienter...", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedContact = spContactsAppel.selectedItem as String
        val contactId = if (selectedContact != "--") contactMap[selectedContact]?.id else null
        val selectedEntreprise = spEntreprisesAppel.selectedItem as Entreprise
        val entrepriseId = entrepriseMap[selectedEntreprise.id]?.id
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")?.toInt()

        val dateAppel = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(etDateAppel.text.toString()) ?: Date()
        val objet = etObjetAppel.text.toString()
        val notes = etNotesAppel.text.toString()
        val appel = Appel(
            contact_id = contactId,
            entreprise_id = entrepriseId,
            date_appel = dateAppel,
            objet = objet,
            notes = notes,
            candidature_id = candidatureId
        )
        DataRepository(applicationContext).saveAppel(appel)
        Log.d("AddAppelActivity", "Appel ajouté")
        finish()
    }

}