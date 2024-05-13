package com.delhomme.jobber

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ContactDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        val contactId = intent.getStringExtra("CONTACT_ID") ?: return

        val contact = DataRepository(this).getContactById(contactId) ?: return

        val contactName = findViewById<TextView>(R.id.contactName)
        val contactEmail = findViewById<TextView>(R.id.contactEmail)
        val contactPhone = findViewById<TextView>(R.id.contactPhone)
        val contactEntreprise = findViewById<TextView>(R.id.contactEntreprise)

        contactName.text = contact.getFullName()
        contactEmail.text = contact.email
        contactPhone.text = contact.telephone
        contactEntreprise.text = contact.entreprise_id

        setTitle("Détail de ${contactName.text}")
    }
}