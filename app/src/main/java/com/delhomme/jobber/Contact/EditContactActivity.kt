package com.delhomme.jobber.contact

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R

class EditContactActivity : AppCompatActivity() {
    private lateinit var etContactFirstName: EditText
    private lateinit var etContactLastName: EditText
    private lateinit var etContactEmail: EditText
    private lateinit var etContactPhone: EditText
    private lateinit var dataRepository: DataRepository
    private var contactId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)

        dataRepository = DataRepository(this)
        contactId = intent.getStringExtra("CONTACT_ID")

        etContactFirstName = findViewById(R.id.etContactFirstName)
        etContactLastName = findViewById(R.id.etContactLastName)
        etContactEmail = findViewById(R.id.etContactEmail)
        etContactPhone = findViewById(R.id.etContactPhone)
        val btnSave = findViewById<Button>(R.id.btnSaveContactChanges)

        contactId?.let {
            dataRepository.getContactById(it)?.let { contact ->
                etContactFirstName.setText(contact.prenom)
                etContactLastName.setText(contact.nom)
                etContactEmail.setText(contact.email)
                etContactPhone.setText(contact.telephone)
            }
        }

        btnSave.setOnClickListener {
            if (saveContactChanges()) {
                Toast.makeText(this, "Contact updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update contact!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveContactChanges(): Boolean {
        val firstName = etContactFirstName.text.toString().trim()
        val lastName = etContactLastName.text.toString().trim()
        val email = etContactEmail.text.toString().trim()
        val phone = etContactPhone.text.toString().trim()

        return contactId?.let {
            val updatedContact = Contact(
                id = it,
                nom = lastName,
                prenom = firstName,
                email = email,
                telephone = phone,
                entrepriseId = "Assume to fetch or unchanged" // Fetch from your data or UI
            )
            dataRepository.saveContact(updatedContact)
            true
        } ?: false
    }
}
