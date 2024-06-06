package com.delhomme.jobber.Activity.Contact

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.R

class AddContactActivity : AppCompatActivity() {
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        entrepriseDataRepository = EntrepriseDataRepository(applicationContext)
        contactDataRepository = ContactDataRepository(applicationContext)

        setupEntrepriseAutoComplete()
    }

    private fun setupEntrepriseAutoComplete() {
        val entreprises = entrepriseDataRepository.loadEntreprises().map { it.nom }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, entreprises)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(adapter)

        setupContactFields(autoCompleteTextView.text.toString())
    }


    private fun setupContactFields(entrepriseNom: String) {
        val etContactPrenom = findViewById<EditText>(R.id.etContactPrenom)
        val etContactNom = findViewById<EditText>(R.id.etContactNom)
        val etContactEmail = findViewById<EditText>(R.id.etContactEmail)
        val etContactTelephone = findViewById<EditText>(R.id.etContactPhone)

        findViewById<Button>(R.id.button_add_contact).setOnClickListener {
            val nom = etContactNom.text.toString()
            val prenom = etContactPrenom.text.toString()
            val email = etContactEmail.text.toString()
            val telephone = etContactTelephone.text.toString()
            val entreprise = entrepriseDataRepository.getOrCreateEntreprise(entrepriseNom)

            val newContact = Contact(
                nom = nom,
                prenom = prenom,
                email = email,
                telephone = telephone,
                entreprise = entreprise.nom,
                appelsIds = mutableListOf()  // Starting with an empty list of calls
            )

            contactDataRepository.updateOrAddItem(contactDataRepository.getItems().toMutableList(), newContact)
            Toast.makeText(this, "Contact added to ${entreprise.nom}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Termine l'activité et retourne à l'activité parente
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}