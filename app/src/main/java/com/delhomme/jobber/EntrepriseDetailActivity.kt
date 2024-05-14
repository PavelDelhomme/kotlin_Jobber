package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.AppelAdapter
import com.delhomme.jobber.adapter.ContactAdapter
import com.delhomme.jobber.models.Appel
import com.delhomme.jobber.models.Contact
import com.delhomme.jobber.models.Entreprise

class EntrepriseDetailActivity : AppCompatActivity() {

    private lateinit var dataRepository: DataRepository
    private lateinit var entreprise: Entreprise
    private lateinit var contactsAdapter: ContactAdapter
    private lateinit var appelsAdapter: AppelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entreprise_detail)

        if(getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        }

        val entrepriseId = intent.getStringExtra("ENTREPRISE_ID") ?: return
        dataRepository = DataRepository(this)
        entreprise = dataRepository.getEntrepriseById(entrepriseId) ?: return

        findViewById<TextView>(R.id.tvEntrepriseName).text = entreprise.nom


        setupRecyclerView()
        setupAddContactButton()
        setupAddAppelButton()
    }

    private fun setupRecyclerView() {
        val contacts = dataRepository.loadContactsForEntreprise(entreprise.id)
        val contactsAdapter = ContactAdapter(contacts, this::onContactClicked, this::onDeleteContactClicked)

        findViewById<RecyclerView>(R.id.rvContacts).apply {
            layoutManager = LinearLayoutManager(this@EntrepriseDetailActivity)
            adapter = contactsAdapter
        }

        val appels = dataRepository.loadAppelsForEntreprise(entreprise.id)
        val appelsAdapter = AppelAdapter(appels, this::onAppelClicked, this::onDeleteAppelClicked)

        findViewById<RecyclerView>(R.id.rvAppels)

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

    private fun onDeleteContactClicked(contactId: String) {
        dataRepository.deleteContact(contactId)
        contactsAdapter.updateContacts(dataRepository.loadContactsForEntreprise(entreprise.id))
    }

    private fun onDeleteAppelClicked(appelId: String) {
        dataRepository.deleteAppel(appelId)
        appelsAdapter.updateAppels(dataRepository.loadAppelsForEntreprise(entreprise.id))
    }

    private fun onAddAppelClicked(view: View) {
        val intent = Intent(this, AddAppelActivity::class.java).apply {
            putExtra("ENTREPRISE_ID", entreprise.id)
        }
        startActivity(intent)
    }

    private fun updateContactList() {
        // TODO ici je suis sensé récupérer les contacts lié à l'entreprise
        val contacts = dataRepository.loadContactsForEntreprise(entreprise.id)
        if (contacts.isNotEmpty()) {
            contactsAdapter.updateContacts(contacts)
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
        if (this::contactsAdapter.isInitialized) {
            updateContactList()
        }
    }
}
