package com.delhomme.jobber.ContactPacket

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.R
import com.google.gson.Gson

class ContactDetailsActivity : AppCompatActivity() {    private lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val txtNom = findViewById<TextView>(R.id.txtNom)
        val txtPrenom = findViewById<TextView>(R.id.txtPrenom)
        val txtPhone = findViewById<TextView>(R.id.txtPhone)
        val txtEmail = findViewById<TextView>(R.id.txtEmail)
        val txtEntreprise = findViewById<TextView>(R.id.txtEntreprise)

        val contactId = intent.getStringExtra("contact_id")

        if (contactId != null) {
            loadContact(contactId)

            txtNom.text = contact.nom
            txtPrenom.text = contact.prenom
            txtPhone.text = contact.telephone
            txtEmail.text = contact.email
            txtEntreprise.text = contact.entrepriseInstance.nom
        } else {
            txtNom.text = "Contact non trouvé"
        }


    }

    private fun loadContact(contactId: String?) {
        val sharedPreferences = getSharedPreferences("contacts_prefs", MODE_PRIVATE)
        val gson = Gson()

        for ((key, value) in sharedPreferences.all) {
            if (key == "contact_$contactId") {
                val contactJson = value as String
                contact = gson.fromJson(contactJson, Contact::class.java)
                break
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
