package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.AppelAdapter
import com.delhomme.jobber.adapter.ContactAdapter
import com.delhomme.jobber.adapter.EntretienAdapter
import com.delhomme.jobber.models.Appel
import com.delhomme.jobber.models.Candidature
import com.delhomme.jobber.models.Contact
import com.delhomme.jobber.models.Entretien

class CandidatureDetailActivity : AppCompatActivity() {

    private lateinit var dataRepository: DataRepository
    private lateinit var candidature: Candidature
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var appelAdapter: AppelAdapter
    private lateinit var entretienAdapter: EntretienAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidature_detail)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val candidatureId = intent.getStringExtra("CANDIDATURE_ID") ?: return
        dataRepository = DataRepository(this)
        candidature = dataRepository.getCandidatureById(candidatureId) ?: return

        findViewById<TextView>(R.id.tvCandidatureInfo).text = "Candidature for ${candidature.titre_offre} at ${candidature.entreprise.nom}"
        findViewById<TextView>(R.id.tvEtatCandidature).text = "Etat : ${candidature.etat.toString()}"
        findViewById<TextView>(R.id.tvNotesCandidature).text = "Notes :\n${candidature.notes.toString()}"
        findViewById<TextView>(R.id.tvLieuPoste).text = "Poste situé à : ${candidature.lieuPoste.toString()}"
        findViewById<TextView>(R.id.tvTypePoste).text = "Poste de type : ${candidature.type_poste}"
        findViewById<TextView>(R.id.tvPlateforme).text = "Candidaté sur : ${candidature.plateforme}"
        setupRecyclerView()
        setupAddContactButton()
        setupAddAppelButton()
        setupAddEntretienButton()
    }
    // TODO Ici je tente d'afficher  les contacts lié à l'entreprise de la candidature
    private fun setupRecyclerView() {
        setupContactRecyclerView()
        setupAppelRecyclerView()
        setupEntretienRecyclerView()
    }

    private fun setupContactRecyclerView() {
        val contacts = dataRepository.loadContactsForEntreprise(candidature.entreprise.id)
        contactAdapter = ContactAdapter(contacts, this::onContactClicked, this::onDeleteContactClicked)

        findViewById<RecyclerView>(R.id.recyclerViewContacts).apply {
            layoutManager = LinearLayoutManager(this@CandidatureDetailActivity)
            adapter = contactAdapter
        }
        updateContactList()
    }

    private fun onContactClicked(contact: Contact) {
        val intent = Intent(this, ContactDetailActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
        }
        startActivity(intent)
    }

    private fun onDeleteContactClicked(contactId: String) {
        dataRepository.deleteContact(contactId)
        contactAdapter.updateContacts(dataRepository.loadContactsForEntreprise(candidature.entreprise.id))
    }
    private fun updateContactList() {
        // TODO Mise à jour de la liste des contacts
        val contacts = dataRepository.loadContactsForEntreprise(candidature.entreprise.id)
        if (contacts.isNotEmpty()) {
            contactAdapter.updateContacts(contacts)
        } else {
            Log.d("CandidatureDetailActivity", "No contacts found for this entreprise.")
        }
    }

    private fun setupAppelRecyclerView() {
        val appels = dataRepository.loadAppelsForCandidature(candidature.id)
        appelAdapter = AppelAdapter(appels, this::onAppelClicked, this::onDeleteAppelClicked)
        Log.d("CandidatureDetailActivity", "liste des appels de la candidautre : ${appels}")

        findViewById<RecyclerView>(R.id.recyclerViewAppels).apply {
            layoutManager = LinearLayoutManager(this@CandidatureDetailActivity)
            adapter = appelAdapter
        }
    }
    private fun onAppelClicked(appel: Appel) {
        Toast.makeText(this, "Appel sélectionné: ${appel.objet}", Toast.LENGTH_LONG).show()
    }
    private fun onDeleteAppelClicked(appelId: String) {
        dataRepository.deleteAppel(appelId)
        val updatedAppels = dataRepository.loadAppelsForCandidature(candidature.id)
        appelAdapter.updateAppels(updatedAppels)
    }

    private fun setupEntretienRecyclerView() {
        val entretiens = dataRepository.loadEntretiensForCandidature(candidature.id)
        entretienAdapter = EntretienAdapter(entretiens, this::onEntretienClicked, this::onDeleteEntretienClicked)

        findViewById<RecyclerView>(R.id.recyclerViewEntretiens).apply {
            layoutManager = LinearLayoutManager(this@CandidatureDetailActivity)
            adapter = entretienAdapter
        }
    }

    private fun onEntretienClicked(entretien: Entretien) {
        Toast.makeText(this, "Entretien sélectionné:  ${entretien.date_entretien}", Toast.LENGTH_LONG).show()
    }

    private fun onDeleteEntretienClicked(entretienId: String) {
        dataRepository.deleteEntretien(entretienId)
        entretienAdapter.updateEntretiens(dataRepository.loadEntretiens())
    }


    private fun setupAddEntretienButton() {
        val btnAddEntretien = findViewById<Button>(R.id.btnAddEntretien)
        btnAddEntretien.setOnClickListener {
            val intent = Intent(this, AddEntretienActivity::class.java).apply {
                putExtra("CANDIDATURE_ID", candidature.id)
            }
            startActivity(intent)
        }
    }

    private fun updateEntretienList() {
        val entretiens = dataRepository.loadEntretiensForCandidature(candidature.id)
        entretienAdapter.updateEntretiens(entretiens)
    }

    private fun setupAddContactButton() {
        val btnAddContact = findViewById<Button>(R.id.btnAddContact)
        btnAddContact.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", candidature.entreprise.id)
            }
            startActivity(intent)
        }
    }

    private fun setupAddAppelButton() {
        val btnAddAppel = findViewById<Button>(R.id.btnAddAppel)
        btnAddAppel.setOnClickListener {
            val intent = Intent(this, AddAppelActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", candidature.entreprise.id)
                putExtra("CANDIDATURE_ID", candidature.id)
            }
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        dataRepository.reloadEntreprises()
        updateEntretienList()
        updateContactList()
    }

}