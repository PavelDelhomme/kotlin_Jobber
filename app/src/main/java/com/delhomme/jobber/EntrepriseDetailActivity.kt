package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.AppelAdapter
import com.delhomme.jobber.adapter.CandidatureAdapter
import com.delhomme.jobber.adapter.ContactAdapter
import com.delhomme.jobber.models.Appel
import com.delhomme.jobber.models.Candidature
import com.delhomme.jobber.models.Contact
import com.delhomme.jobber.models.Entreprise

class EntrepriseDetailActivity : AppCompatActivity() {

    private lateinit var dataRepository: DataRepository
    private lateinit var entreprise: Entreprise
    private lateinit var contactsAdapter: ContactAdapter
    private lateinit var appelsAdapter: AppelAdapter
    private lateinit var candidaturesAdapter: CandidatureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entreprise_detail)

        if(supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val entrepriseId = intent.getStringExtra("ENTREPRISE_ID") ?: return
        dataRepository = DataRepository(this)
        entreprise = dataRepository.getEntrepriseById(entrepriseId) ?: return

        contactsAdapter = ContactAdapter(emptyList(), dataRepository, this::onContactClicked, this::onDeleteContactClicked)
        appelsAdapter = AppelAdapter(emptyList(), this::onAppelClicked, this::onDeleteAppelClicked)

        findViewById<TextView>(R.id.tvEntrepriseName).text = entreprise.nom

        setupRecyclerView()
        setupAddContactButton()
        setupAddAppelButton()
    }

    private fun setupRecyclerView() {
        setupContactRecyclerView()
        setupAppelRecyclerView()
        setupCandidatureRecyclerView()
    }
    private fun setupContactRecyclerView() {
        val contacts = dataRepository.loadContactsForEntreprise(entreprise.id)
        val contactsAdapter = ContactAdapter(contacts, dataRepository, this::onContactClicked, this::onDeleteContactClicked)
        findViewById<RecyclerView>(R.id.rvContacts).apply {
            layoutManager = LinearLayoutManager(this@EntrepriseDetailActivity)
            adapter = contactsAdapter
        }
    }

    private fun setupAppelRecyclerView() {
        val appels = dataRepository.loadAppelsForEntreprise(entreprise.id)
        val appelsAdapter = AppelAdapter(appels, this::onAppelClicked, this::onDeleteAppelClicked)

        findViewById<RecyclerView>(R.id.rvAppels).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appelsAdapter
        }
    }

    private fun setupCandidatureRecyclerView() {
        val candidatures = dataRepository.loadCandidaturesForEntreprise(entreprise.id)
        val candidaturesAdapter = CandidatureAdapter(candidatures, dataRepository, this::onCandidatureClicked, this::onDeleteCandidatureClicked)

        findViewById<RecyclerView>(R.id.rvCandidatures).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = candidaturesAdapter
        }
    }
    private fun onContactClicked(contact: Contact) {
        val intent = Intent(this, ContactDetailActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
        }
        startActivity(intent)
    }

    private fun onAppelClicked(appel: Appel) {
        val intent = Intent(this, AppelDetailActivity::class.java).apply {
            putExtra("APPEL_ID", appel.id)
        }
        startActivity(intent)
    }

    private fun onCandidatureClicked(candidature: Candidature) {
        val intent = Intent(this, CandidatureDetailActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidature.id)
        }
        startActivity(intent)
    }

    private fun onDeleteContactClicked(contactId: String) {
        dataRepository.deleteContact(contactId)
        Log.d("EntrepriseDetailActivity", "onDeleteContactClicked")
        Log.d("EntrepriseDetailActivity", "contactId : $contactId")
        contactsAdapter.updateContacts(dataRepository.loadContactsForEntreprise(entreprise.id))
        dataRepository.loadEntreprises()
        Log.d("EntrepriseDetailActivity", "contactsAdapter updated")
    }

    private fun onDeleteAppelClicked(appelId: String) {
        Log.d("onDeleteAppelClicked", "Delete appel clicked")
        dataRepository.deleteAppel(appelId)
        val updatedAppels = dataRepository.loadAppelsForEntreprise(entreprise.id)
        appelsAdapter.updateAppels(updatedAppels)
    }

    private fun onDeleteCandidatureClicked(candidatureId: String) {
        dataRepository.deleteCandidature(candidatureId)
        updateCandidaturesList()
    }


    private fun updateContactList() {
        // TODO ici je suis sensé récupérer les contacts lié à l'entreprise
        val contacts = dataRepository.loadContactsForEntreprise(entreprise.id)
        if (contacts.isNotEmpty()) {
            contactsAdapter.updateContacts(contacts)
        } else {
            Log.d("EntrepriseDetailActivity", "No contacts found for this entreprise.")
        }
    }

    private fun updateCandidaturesList() {
        val candidatures = dataRepository.loadCandidaturesForEntreprise(entreprise.id)
        if (candidatures.isNotEmpty()) {
            candidaturesAdapter.updateCandidatures(candidatures)
        } else {
            Log.d("EntrepriseDetailActivity", "No candidature found for this entreprise.")
        }
    }

    private fun setupAddContactButton() {
        findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", entreprise.id)
            }
            startActivity(intent)
        }
    }

    private fun setupAddAppelButton() {
        findViewById<Button>(R.id.btnAddAppel).setOnClickListener {
            val intent = Intent(this, AddAppelActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", entreprise.id)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        dataRepository.reloadEntreprises()
        reloadAppelsAndContacts()
        if (this::contactsAdapter.isInitialized) {
            updateContactList()
        }
        if (this::candidaturesAdapter.isInitialized) {
            updateCandidaturesList()
        }
    }

    private fun reloadAppelsAndContacts() {
        if (this::contactsAdapter.isInitialized && this::appelsAdapter.isInitialized) {
            val contacts = dataRepository.loadContactsForEntreprise(entreprise.id)
            contactsAdapter.updateContacts(contacts)

            val appels = dataRepository.loadAppelsForEntreprise(entreprise.id)
            appelsAdapter.updateAppels(appels)
        }
    }
}
