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
                Log.d("AddAppelActivity", "selectedEntreprise : ${selectedEntreprise}")
                Log.d("AddAppelActivity", "selectedEntreprise.nom : ${selectedEntreprise.nom}")
                Log.d("AddAppelActivity", "selectedEntreprise.id : ${selectedEntreprise.id}")
                updateContactSpinner(selectedEntreprise.id)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionnellement, nettoyez le spinner des contacts si rien n'est sélectionné
                spContactsAppel.adapter = ArrayAdapter<String>(this@AddAppelActivity, android.R.layout.simple_spinner_dropdown_item, listOf("--"))
                Log.d("AddAppelActivity", "onNothingSelected apply")
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
        Log.d("AddAppelActivity", "handleIntent | entrepriseId : $entrepriseId")

        // Charger les entreprises
        val entreprises = dataRepository.loadEntreprises()
        Log.d("AddAppelActivity", "handleIntent | entreprises : $entreprises")
        //entrepriseMap = entreprises.associateBy { it.nom }
        entrepriseMap = entreprises.associateBy { it.id }
        Log.d("AddAppelActivity", "handleIntent | entrepriseMap $entrepriseMap")

        // Configurer le spinner des entreprises
        val entrepriseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, entreprises)
        Log.d("AddAppelActivity", "handleIntent | entrepriseAdapter ${entrepriseAdapter}")
        spEntreprisesAppel.adapter = entrepriseAdapter
        Log.d("AddAppelActivity", "handleIntent | spEntreprisesAppel : $spEntreprisesAppel")
        Log.d("AddAppelActivity", "handleIntent | spEntreprisesAppel.adapter : ${spEntreprisesAppel.adapter}")

        // Gérer la sélection initiale et la possibilité de changer d'entreprise
        if (entrepriseId != null) {
            Log.d("AddAppelActivity", "entrepriseId != null")
            val index = entreprises.indexOfFirst { it.id == entrepriseId }
            Log.d("AddAppelActivity", "index : $index")
            if (index != -1) {
                Log.d("AddAppelActivity", "index != -1")
                spEntreprisesAppel.setSelection(index)
                Log.d("AddAppelActivity", "spEntreprisesAppel.setSelection(index) : ${spEntreprisesAppel.setSelection(index)}")
                Log.d("AddAppelActivity", "spEntreprisesAppel.setSelection(index).toString() : ${spEntreprisesAppel.setSelection(index).toString()}")
                spEntreprisesAppel.isEnabled = false
                Log.d("AddAppelActivity", "spEntreprisesAppel.isEnabled : ${spEntreprisesAppel.isEnabled}")
                updateContactSpinner(entrepriseId)
                Log.d("AddAppelActivity", "updateContactSpinner(entrepriseId) used")
            }
        }
        // Charger les contacts selon l'entreprise, si spécifiée
        val contacts = entrepriseId?.let { dataRepository.loadContactsForEntreprise(it) } ?: listOf()
        Log.d("AddAppelActivity", "entrepriseId?.let { dataRepository.loadContactsForEntreprise(it) } ?: listOf() \nNOUS DONNE\n" +
                "${entrepriseId?.let { dataRepository.loadContactsForEntreprise(it) } ?: listOf()}")
        contactMap = contacts.associateBy { it.id }
        Log.d("AddAppelActivity", "contactMap : $contactMap")
        Log.d("AddAppelActivity", "contactMap.toString() : ${contactMap.toString()}")
        // Mettre à jour le spinner des contacts
        updateContactSpinner(null) // Pour initialiser avec "--"
        Log.d("AddAppelActivity", "updateContactSpinner(null) loaded")
    }
    private fun updateContactSpinner(entrepriseId: String?) {
        Log.d("AddAppelActivity", "updateContactSpinner : entrepriseId : $entrepriseId")
        val dataRepository = DataRepository(this)
        val contacts = dataRepository.loadContactsForEntreprise(entrepriseId)
        Log.d("AddAppelActivity", "updateContactSpinner : contacts : ${contacts}")
        val contactNames = mutableListOf("--")
        Log.d("AddAppelActivity", "updateContactSpinner : contactNames : ${contactNames}")
        contactNames.addAll(contacts.map { it.getFullName() })
        Log.d("AddAppelActivity", "updateContactSpinner : contactNames.addAll(contacts.map { it.getFullName() }) : ${contactNames.addAll(contacts.map { it.getFullName() })}")
        spContactsAppel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contactNames)
        Log.d("AddAppelActivity", "updateContactSpinner : spContactsAppel.adapter : ${spContactsAppel.adapter}")
        contactMap = contacts.associateBy { it.getFullName() }
        Log.d("AddAppelActivity", "updateContactSpinner : contactMap : ${contactMap}")
    }

    private fun addAppel(view: View) {
        // S'assurer que les maps sont initialisées
        Log.d("AddAppelActivity", "Into addAppel")
        if (!::contactMap.isInitialized || !::entrepriseMap.isInitialized) {
            Log.d("AddAppelActivity", "        if (!::contactMap.isInitialized || !::entrepriseMap.isInitialized) ")
            Log.d("AddAppelActivity", "(!::contactMap.isInitialized || !::entrepriseMap.isInitialized) nous donne :\n" +
                    "${(!::contactMap.isInitialized || !::entrepriseMap.isInitialized)}")
            Toast.makeText(this, "Les données sont en cours de chargement, veuillez patienter...", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedContactName = spContactsAppel.selectedItem.toString()
        //Log.d("AddAppelActivity", "addAppel : selectedContactName = $selectedContactName")
        //Log.d("AddAppelActivity", "addAppel : selectedContactName.toString() = ${selectedContactName.toString()}")
        //Log.d("AddAppelActivity", "addAppel : contactMap[selectedContactName] : ${contactMap[selectedContactName]}")
        //Log.d("AddAppelActivity", "addAppel : contactMap[selectedContactName]?.nom : ${contactMap[selectedContactName]?.nom}")
        //Log.d("AddAppelActivity", "addAppel : contactMap[selectedContactName]?.id : ${contactMap[selectedContactName]?.id}")
        //Log.d("AddAppelActivity", "addAppel : contactMap[selectedContactName]!!.id : ${contactMap[selectedContactName]!!.id}")
        val contactId = if (selectedContactName != "--") contactMap[selectedContactName]?.id else null
        //Log.d("AddAppelActivity", "AddAppel : Contact sélectionné : ${contactId}")
        val selectedEntreprise = spEntreprisesAppel.selectedItem as? Entreprise
        //Log.d("AddAppelActivity", "AddAppel : selectedEntrepriseId : ${selectedEntreprise}")
        val entrepriseId = entrepriseMap[selectedEntreprise?.id]?.id
        //Log.d("AddAppelActivity", "AddAppel : Entreprise sélectionnée : ${entrepriseId}")
        if (entrepriseId == null) {
            Log.d("AddAppelActivity", "addAppel : if (entrepriseId == null)")
            Toast.makeText(this, "Erreur: entreprise non sélectionnée ou non trouvée.", Toast.LENGTH_SHORT).show()
            return
        }

        val dateAppel = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(etDateAppel.text.toString()) ?: Date()
        Log.d("AddAppelActivity", "AddAppel : dateAppel : $dateAppel")
        val objet = etObjetAppel.text.toString()
        Log.d("AddAppelActivity", "AddAppel : objet : $objet")
        val notes = etNotesAppel.text.toString()
        Log.d("AddAppelActivity", "AddAppel : notes : $notes")
        val appel = Appel(
            contact_id = contactId,
            entreprise_id = entrepriseId,
            date_appel = dateAppel,
            objet = objet,
            notes = notes,
            candidature_id = intent.getStringExtra("CANDIDATURE_ID")
        )
        Log.d("AddAppelActivity", "AddAppel : appel : $appel")
        Log.d("AddAppelActivity", "AddAppel : appel : ${appel.contact_id}")
        Log.d("AddAppelActivity", "AddAppel : appel : ${appel.entreprise_id}")
        Log.d("AddAppelActivity", "AddAppel : appel : ${appel.date_appel}")
        Log.d("AddAppelActivity", "AddAppel : appel : ${appel.objet}")
        Log.d("AddAppelActivity", "AddAppel : appel : ${appel.notes}")
        Log.d("AddAppelActivity", "AddAppel : appel : ${appel.candidature_id}")
        DataRepository(applicationContext).saveAppel(appel)

        val candidature = DataRepository(applicationContext).getCandidatureById(appel.candidature_id!!)
        candidature?.appelsIds?.add(appel.id)
        DataRepository(applicationContext).saveCandidature(candidature!!)
        Log.d("AddAppelActivity", "Appel ajouté a la candidature : ${candidature?.appelsIds}")
        /*
        if (contactId != null) {
            Log.d("AddAppelActivity", "contactId != null | contact id : $contactId")
            var contactAppel = DataRepository(applicationContext).getContactById(contactId.toString())
            Log.d("AddAppelActivity", "contactAppel : ${contactAppel}")
            DataRepository(applicationContext).addAppelToContact(appel, contactId)
            Log.d("AddAppelActivity", "Appel pour ${contactAppel?.nom}")
        }
        Log.d("AddAppelActivity", "Appel ajouté")
        var appelEnregistre = DataRepository(applicationContext).getAppelById(appel.id)
        var entrepriseAppel = DataRepository(applicationContext).getEntrepriseById(entrepriseId)
        var candidatureAppel = DataRepository(applicationContext).getCandidatureById(intent.getStringExtra("CANDIDATURE_ID").toString())
        Log.d("AddAppelActivity", "Instance d'appel : ${appelEnregistre}")
        Log.d("AddAppelActivity", "Entreprise de l'appel : ${entrepriseAppel?.nom}")
        Log.d("AddAppelActivity", "Instance de l'entreprise : ${entrepriseAppel}")
        Log.d("AddAppelActivity", "Instance de la candidature si il y a : ${candidatureAppel?.titre_offre}")*/
        contactId?.let {
            DataRepository(applicationContext).addAppelToContact(appel, it)
        }
        finish()
    }

}