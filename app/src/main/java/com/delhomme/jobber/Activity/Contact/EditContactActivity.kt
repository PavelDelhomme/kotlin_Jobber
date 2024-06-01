package com.delhomme.jobber.Activity.Contact

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.R

class EditContactActivity : AppCompatActivity() {
    private lateinit var etContactPrenom: EditText
    private lateinit var etContactNom: EditText
    private lateinit var etContactEmail: EditText
    private lateinit var etContactPhone: EditText
    private lateinit var actvEntreprise: AutoCompleteTextView
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private var contactId: String? = null
    private var entrepriseId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        contactDataRepository = ContactDataRepository(this)
        entrepriseDataRepository = EntrepriseDataRepository(this)
        contactId = intent.getStringExtra("CONTACT_ID")
        entrepriseId = intent.getStringExtra("ENTREPRISE_ID")
        
        if (contactId == null) {
            Toast.makeText(this, "Erreur: ID de contact manquant.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        
        setupEntrepriseAutoComplete()
        setupFields()

        findViewById<Button>(R.id.btnSaveContactChanges).setOnClickListener {
            if (saveChanges()) {
                Toast.makeText(this, "Modifications enregistrées.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        findViewById<Button>(R.id.btnCancelContactChanges).setOnClickListener {
            finish()
        }
    }

    private fun setupEntrepriseAutoComplete() {
        val entreprises = entrepriseDataRepository.getItems().map { it.nom }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises)
        findViewById<AutoCompleteTextView>(R.id.actvNomEntreprise).apply {
            setAdapter(adapter)
        }
    }

    private fun setupFields() {
        val contact = contactDataRepository.findByCondition { it.id == contactId }.firstOrNull()
        contact?.let { cont ->
            etContactPrenom = findViewById<EditText>(R.id.etContactPrenom).apply { setText(cont.prenom) }
            etContactNom = findViewById<EditText>(R.id.etContactNom).apply { setText(cont.nom) }
            etContactEmail = findViewById<EditText>(R.id.etContactEmail).apply { setText(cont.email) }
            etContactPhone = findViewById<EditText>(R.id.etContactPhone).apply { setText(cont.telephone) }
            actvEntreprise.setText(cont.entreprise)
        }
    }

    private fun saveChanges(): Boolean {
        val prenom = etContactPrenom.text.toString()
        val nom = etContactNom.text.toString()
        val email = etContactEmail.text.toString()
        val phone = etContactPhone.text.toString()
        val entrepriseNom = actvEntreprise.text.toString()

        val entreprise = entrepriseDataRepository.getOrCreateEntreprise(entrepriseNom)

        val updatedContact = Contact(
            id = contactId!!,
            prenom = prenom,
            nom = nom,
            email = email,
            telephone = phone,
            entreprise = entreprise.nom
        )

        contactDataRepository.addOrUpdateContact(updatedContact)
        return true
    }
}
