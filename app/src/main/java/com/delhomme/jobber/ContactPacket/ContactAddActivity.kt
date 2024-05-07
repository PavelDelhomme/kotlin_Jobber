package com.delhomme.jobber.ContactPacket

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.EntreprisePacket.Entreprise
import com.delhomme.jobber.R
import com.google.gson.Gson
import java.util.UUID

class ContactAddActivity : AppCompatActivity() {
    private lateinit var spinnerEntreprise: Spinner
    private lateinit var etNouvelleEntreprise: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_add)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ajouter Contact"

        spinnerEntreprise = findViewById(R.id.spinnerEntreprises)
        etNouvelleEntreprise = findViewById(R.id.etNouvelleEntreprise)

        val cancelButton = findViewById<Button>(R.id.btnCancel)
        val addButton = findViewById<Button>(R.id.btnAdd)

        cancelButton.setOnClickListener { finish() }
        addButton.setOnClickListener { ajouterContact() }

        loadEntreprises()
    }

    private fun loadEntreprises() {
        val sharedPreferences = getSharedPreferences("entreprises_prefs", MODE_PRIVATE)
        val gson = Gson()

        val entreprises = sharedPreferences.all.mapNotNull { entry ->
            gson.fromJson(entry.value as String, Entreprise::class.java)
        }

        val nomsEntreprises = entreprises.map { it.nom }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nomsEntreprises)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEntreprise.adapter = adapter
    }

    private fun ajouterContact() {
        val nomContact = findViewById<EditText>(R.id.etName).text.toString()
        val prenomContact = findViewById<EditText>(R.id.etSurname).text.toString()
        val telephoneContact = findViewById<EditText>(R.id.etPhone).text.toString()
        val emailContact = findViewById<EditText>(R.id.etMail).text.toString()
        val nomEntrepriseSelectionnee = spinnerEntreprise.selectedItem?.toString() ?: ""
        val nomNouvelleEntreprise = etNouvelleEntreprise.text.toString()

        // Vérification de validité des entrées
        if (nomContact.isBlank() || prenomContact.isBlank() || telephoneContact.isBlank()) {
            Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show()
            return
        }

        val entrepriseNom = if (nomNouvelleEntreprise.isNotBlank()) {
            nomNouvelleEntreprise
        } else {
            nomEntrepriseSelectionnee
        }
        val entreprise = findOrCreateEntreprise(entrepriseNom)

        if (contactExists(nomContact, prenomContact, entreprise.nom)) {
            Toast.makeText(this, "Ce contact existe déjà.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val contactId = generateId()

        val contact = Contact(
            id = contactId,
            nom = nomContact,
            prenom = prenomContact,
            telephone = telephoneContact,
            email = emailContact,
            entrepriseNom = entreprise.nom,
            entrepriseInstance = entreprise
        )

        saveContact(contact)

        Toast.makeText(this, "Contact ajoutée avec succès !", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun contactExists(nom: String, prenom: String, entreprise: String): Boolean {
        val sharedPreferences = getSharedPreferences("contacts_prefs", MODE_PRIVATE)
        val gson = Gson()

        for ((_, value) in sharedPreferences.all) {
            val contactJson = value as String
            val contact = gson.fromJson(contactJson, Contact::class.java)
            if (contact.nom.equals(nom, ignoreCase = true) && contact.prenom.equals(prenom, ignoreCase = true) && contact.entrepriseNom.equals(entreprise, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun findOrCreateEntreprise(nom: String): Entreprise {
        val sharedPreferences = getSharedPreferences("entreprises_prefs", MODE_PRIVATE)
        val gson = Gson()

        for ((_, value) in sharedPreferences.all) {
            val entrepriseJson = value as String
            val entreprise = gson.fromJson(entrepriseJson, Entreprise::class.java)
            if (entreprise.nom.equals(nom, ignoreCase = true)){
                return entreprise
            }
        }

        val nouvelleEntreprise = Entreprise(
            id = generateId(),
            nom = nom,
            localisation = null,
            secteurActivite = null,
            description = null,
            email = null,
            siteEntreprise = null
        )

        saveEntreprise(nouvelleEntreprise)

        return nouvelleEntreprise
    }

    private fun saveEntreprise(entreprise: Entreprise) {
        val sharedPreferences = getSharedPreferences("entreprises_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val entrepriseJson = gson.toJson(entreprise)
        editor.putString("entreprise_${entreprise.id}", entrepriseJson)
        editor.apply()
    }

    private fun saveContact(contact: Contact) {
        val sharedPreferences = getSharedPreferences("contacts_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val contactJson = gson.toJson(contact)
        editor.putString("contact_${contact.id}", contactJson)
        editor.apply()

        Log.e("saveContact", "Contact sauvegardé : $contactJson")
    }

    private fun generateId(): String {
        return UUID.randomUUID().toString()
    }
}