package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.models.Contact

class ContactDetailActivity : AppCompatActivity() {
    private lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        if(getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }
        val contactId = intent.getStringExtra("CONTACT_ID") ?: return

        contact = DataRepository(this).getContactById(contactId) ?: return

        val contactName = findViewById<TextView>(R.id.contactName)
        val contactEmail = findViewById<TextView>(R.id.emailContact)
        val contactPhone = findViewById<TextView>(R.id.telephoneContact)
        val contactEntreprise = findViewById<TextView>(R.id.contactEntreprise)

        val contactEntrepriseNom = DataRepository(this).getEntrepriseById(contact.entreprise.id)

        contactName.text = contact.getFullName()
        contactEmail.text = contact.email
        contactPhone.text = contact.telephone
        contactEntreprise.text = contactEntrepriseNom?.nom

        setTitle("Détails de ${contactName.text}")
    }

    fun onAddAppelClicked(view: View) {
        val intent = Intent(this, AddAppelActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
            putExtra("ENTREPRISE_ID", contact.entreprise.id)
        }
        startActivity(intent)
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