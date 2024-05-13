package com.delhomme.jobber

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ContactDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        if(getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }
        val contactId = intent.getStringExtra("CONTACT_ID") ?: return

        val contact = DataRepository(this).getContactById(contactId) ?: return

        val contactName = findViewById<TextView>(R.id.contactName)
        val contactEmail = findViewById<TextView>(R.id.emailContact)
        val contactPhone = findViewById<TextView>(R.id.telephoneContact)
        val contactEntreprise = findViewById<TextView>(R.id.contactEntreprise)

        val contactEntrepriseNom = DataRepository(this).getEntrepriseById(contact.entreprise.id)

        contactName.text = contact.getFullName()
        contactEmail.text = contact.email
        contactPhone.text = contact.telephone
        contactEntreprise.text = contactEntrepriseNom?.nom

        setTitle("Détail de ${contactName.text}")
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