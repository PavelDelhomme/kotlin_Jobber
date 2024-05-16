package com.delhomme.jobber.Appel

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.Entreprise.model.Entreprise
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_appel)

        if(getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }
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
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")

        val entreprises = dataRepository.loadEntreprises()
        entrepriseMap = entreprises.associateBy { it.id }
        val entrepriseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, entreprises)
        spEntreprisesAppel.adapter = entrepriseAdapter

        if (entrepriseId != null) {
            val index = entreprises.indexOfFirst { it.id == entrepriseId }
            if (index != -1) {
                spEntreprisesAppel.setSelection(index)
                spEntreprisesAppel.isEnabled = candidatureId == null
            }
            updateContactSpinner(entrepriseId)
        }
        // Charger les contacts selon l'entreprise, si spécifiée
        val contacts = entrepriseId?.let { dataRepository.loadContactsForEntreprise(it) } ?: listOf()
        contactMap = contacts.associateBy { it.id }
        updateContactSpinner(null) // Pour initialiser avec "--"
    }
    private fun updateContactSpinner(entrepriseId: String?) {
        val contacts = DataRepository(this).loadContactsForEntreprise(entrepriseId)
        val contactNames = contacts.map { it.getFullName() }.toMutableList()
        contactNames.add(0, "--")

        spContactsAppel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contactNames)
        contactMap = contacts.associateBy { it.getFullName() }
    }

    private fun addAppel(view: View) {
        val selectedContactName = spContactsAppel.selectedItem.toString()
        val contactId = if (selectedContactName != "--") contactMap[selectedContactName]?.id else null
        val selectedEntreprise = spEntreprisesAppel.selectedItem as? Entreprise
        val entrepriseId = selectedEntreprise?.id
        val candidatureId = intent.getStringExtra("CANDIDATURE_ID")
        /*if (contactId != null) {
            Toast.makeText(this, "Veuillez sélectionner un contact valide.", Toast.LENGTH_SHORT).show()
            Log.d("addAppel", "contactId != null | Veuillez sélectionner un contact valide")
            Log.d("addAppel", "contactId : $contactId | selectedContactName : $selectedContactName")
            return
        }*/
        /*if (entrepriseId == null) {
            Toast.makeText(this, "Erreur : entreprise non sélectionnée ou non trouvée", Toast.LENGTH_SHORT).show()
            Log.d("addAppel", "entrepriseId == null | Veuillez sélectionner une entreprise valide")
            Log.d("addAppel", "entrepriseId : $entrepriseId | selecteEntreprise : $selectedEntreprise")
            return
        }*/

        if (entrepriseId == null) {
            Toast.makeText(this, "Erreur: entreprise non sélectionnée ou non trouvée.", Toast.LENGTH_LONG)
            Log.d("addAppel", "Erreur: entreprise non sélectionnée ou non trouvée.")
            return
        }
        try {
            val dateAppel = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(etDateAppel.text.toString()) ?: Date()
            val objet = etObjetAppel.text.toString()
            val notes = etNotesAppel.text.toString()
            val appel = Appel(contact_id = contactId, entreprise_id = entrepriseId, date_appel = dateAppel, objet = objet, notes = notes, candidature_id = candidatureId)
            DataRepository(applicationContext).saveAppel(appel)

            if (candidatureId != null) {
                val candidature = DataRepository(applicationContext).getCandidatureById(candidatureId)
                candidature?.appelsIds?.add(appel.id)
                DataRepository(applicationContext).saveCandidature(candidature!!)
            }
            if (contactId != null) {
                val contact = DataRepository(applicationContext).getContactById(contactId)
                contact?.appelsIds?.add(appel.id)
                DataRepository(applicationContext).saveContact(contact!!)
            }

            finish()
        } catch (e: ParseException) {
            Toast.makeText(this, "Format de date invalide", Toast.LENGTH_SHORT).show()
            Log.e("addAppel", "Erreur format de date invalide")
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