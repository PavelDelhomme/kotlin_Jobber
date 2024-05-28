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
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.Model.Entreprise
import com.delhomme.jobber.R

class AddContactActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        if(getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }

        val dataRepository = DataRepository(this)
        val entreprises = dataRepository.getEntreprises()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entreprises.map { it.nom })
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(adapter)

        var entrepriseId = intent.getStringExtra("ENTREPRISE_ID")
        var entreprise: Entreprise? = null

        if (entrepriseId != null) {
            entreprise = dataRepository.getEntrepriseByNom(entrepriseId)
        }

        val etContactPrenom = findViewById<EditText>(R.id.etContactPrenom)
        val etContactNom = findViewById<EditText>(R.id.etContactNom)
        val etContactEmail = findViewById<EditText>(R.id.etContactEmail)
        val etContactTelephone = findViewById<EditText>(R.id.etContactPhone)

        val etCompanyName = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        etCompanyName.setText(entreprise?.nom)

        findViewById<Button>(R.id.button_add_contact).setOnClickListener {

            val nom = etContactNom.text.toString()
            val prenom = etContactPrenom.text.toString()
            val email = etContactEmail.text.toString()
            val telephone = etContactTelephone.text.toString()
            val entrepriseNom = autoCompleteTextView.text.toString()

            val entreprise = entreprise ?: dataRepository.getOrCreateEntreprise(entrepriseNom)
            val contact = Contact(
                nom = nom,
                prenom = prenom,
                email = email,
                telephone = telephone,
                entrepriseNom = entreprise.nom,
                appelsIds = mutableListOf()
            )

            dataRepository.addContactToEntreprise(contact.id, entreprise.nom)
            dataRepository.saveEntreprise(entreprise)
            dataRepository.saveContact(contact)

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