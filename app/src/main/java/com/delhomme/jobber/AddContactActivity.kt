package com.delhomme.jobber

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.models.Contact

class AddContactActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val etContactNom = findViewById<EditText>(R.id.etContactName)
        val etContactNom2 = findViewById<EditText>(R.id.etContactSurname)
        val etContactEmail = findViewById<EditText>(R.id.etContactEmail)
        val etContactTelephone = findViewById<EditText>(R.id.etContactPhone)
        val etCompanyName = findViewById<EditText>(R.id.etCompanyName)
        val btnSaveContact = findViewById<Button>(R.id.btnSaveContact)
        btnSaveContact.setOnClickListener {
            val nom = etContactNom.text.toString()
            val prenom = etContactNom2.text.toString()
            val email = etContactEmail.text.toString()
            val telephone = etContactTelephone.text.toString()
            val entrepriseNom = etCompanyName.text.toString()

            val dataRepository = DataRepository(this)
            val entreprise = dataRepository.getOrCreateEntreprise(entrepriseNom)
            val contact = Contact(nom = nom, prenom = prenom, email = email, telephone = telephone, entreprise_id = entreprise.id)

            dataRepository.addContactToEntreprise(contact, entreprise.id)

            Toast.makeText(this, "Contact added to ${entreprise.nom}", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}