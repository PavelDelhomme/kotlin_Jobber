package com.delhomme.jobber.Contact

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R

class EditContactActivity : AppCompatActivity() {
    private lateinit var dataRepository: DataRepository
    private var contactId: String? = null
    private var entrepriseId: String? = null

    private lateinit var etContactPrenom: EditText
    private lateinit var etContactNom: EditText
    private lateinit var etContactEmail: EditText
    private lateinit var etContactPhone: EditText
    private lateinit var actvEntreprise: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataRepository = DataRepository(this)
        contactId = intent.getStringExtra("CONTACT_ID")
        entrepriseId = intent.getStringExtra("ENTREPRISE_ID")

        etContactPrenom = findViewById(R.id.etContactPrenom)
        etContactNom = findViewById(R.id.etContactNom)
        etContactEmail = findViewById(R.id.etContactEmail)
        etContactPhone = findViewById(R.id.etContactPhone)
        actvEntreprise = findViewById(R.id.actvNomEntreprise)
        val btnSave = findViewById<Button>(R.id.btnSaveContactChanges)

        setupEntrepriseAutoComplete()
        setupFields()

        btnSave.setOnClickListener {
            if (saveContactChanges()) {
                Toast.makeText(this, "Contact updated successfully!", Toast.LENGTH_SHORT).show()
                LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent("CONTACTS_UPDATED"))
                finish()
            } else {
                Toast.makeText(this, "Failed to update contact!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupEntrepriseAutoComplete() {
        val entreprises = dataRepository.getEntreprises().map { it.nom }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises)
        actvEntreprise.setAdapter(adapter)
    }

    private fun setupFields() {
        contactId?.let {
            val contact = dataRepository.getContactById(it)
            if (contact != null) {
                etContactPrenom.setText(contact.prenom)
                etContactNom.setText(contact.nom)
                etContactEmail.setText(contact.email)
                etContactPhone.setText(contact.telephone)
                actvEntreprise.setText(contact.entrepriseNom)
            } else {
                Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveContactChanges(): Boolean {
        val prenom = etContactPrenom.text.toString().trim()
        val nom = etContactNom.text.toString().trim()
        val email = etContactEmail.text.toString().trim()
        val phone = etContactPhone.text.toString().trim()
        val entrepriseNom = actvEntreprise.text.toString().trim()

        if (contactId != null) {
            val existingContact = dataRepository.getContactById(contactId!!)
            val appelsIds = existingContact?.appelsIds ?: mutableListOf()
            val candidatureIds = existingContact?.candidatureIds ?: mutableListOf()

            dataRepository.editContact(
                contactId!!,
                nom,
                prenom,
                email,
                phone,
                entrepriseNom,
                appelsIds,
                candidatureIds
            )
            return true
        }
        return false
    }
}
