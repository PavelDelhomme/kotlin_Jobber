package com.delhomme.jobber.Contact

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Appel.AddAppelActivity
import com.delhomme.jobber.Appel.DetailsAppelActivity
import com.delhomme.jobber.Appel.EditAppelActivity
import com.delhomme.jobber.Appel.adapter.AppelAdapter
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R

class DetailsContactActivity : AppCompatActivity() {
    private lateinit var dataRepository: DataRepository
    private lateinit var contact: Contact
    private lateinit var appelAdapter: AppelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_contact)

        if(getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }
        val contactId = intent.getStringExtra("CONTACT_ID") ?: return

        dataRepository = DataRepository(this)

        contact = dataRepository.getContactById(contactId) ?: return

        val contactNomComplet = findViewById<TextView>(R.id.contactNomComplet)
        val contactEmail = findViewById<TextView>(R.id.emailContact)
        val contactPhone = findViewById<TextView>(R.id.telephoneContact)
        val contactEntreprise = findViewById<TextView>(R.id.contactEntreprise)

        val entreprise = contact.entrepriseNom?.let { dataRepository.getEntrepriseByNom(it)}

        contactNomComplet.text = contact.getFullName()
        contactEmail.text = contact.email
        contactPhone.text = contact.telephone
        contactEntreprise.text = entreprise?.nom ?: "Unknown"

        setTitle("Détails de ${contactNomComplet.text}")

        setupRecyclerView()
        setupAddAppelButton()
    }

    private fun setupRecyclerView() {
        setupAppelRecyclerView()
    }
    private fun setupAppelRecyclerView() {
        val appels = dataRepository.loadAppelsForContact(contact.id)
        appelAdapter = AppelAdapter(appels, dataRepository, this::onAppelClicked, this::onDeleteAppelClicked, this::onEditAppelClicked)

        findViewById<RecyclerView>(R.id.recyclerViewAppels).apply {
            layoutManager = LinearLayoutManager(this@DetailsContactActivity)
            adapter = appelAdapter
        }
        updateAppelList()
    }

    private fun onAppelClicked(appel: Appel) {
        val intent = Intent(this, DetailsAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appel.id)
        }
        startActivity(intent)
    }

    private fun onDeleteAppelClicked(appelId: String) {
        dataRepository.deleteAppel(appelId)
        updateAppelList()
    }

    private fun onEditAppelClicked(appelId: String) {
        val intent = Intent(this, EditAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appelId)
            putExtra("ENTREPRISE_ID", contact.entrepriseNom)
            putExtra("CONTACT_ID", contact.id)
        }
        startActivity(intent)
        updateAppelList()
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

    private fun updateAppelList() {
        val appels = dataRepository.loadAppelsForContact(contact.id)
        if (appels.isNotEmpty()) {
            appelAdapter.updateAppels(appels)
        } else {
            appelAdapter.updateAppels(emptyList())
            Log.d("DetailsContactActivity", "No appels found for this contact.")
        }
    }

    private fun setupAddAppelButton() {
        findViewById<Button>(R.id.btnAddAppel).setOnClickListener {
            val intent = Intent(this, AddAppelActivity::class.java).apply {
                putExtra("CONTACT_ID", contact.id)
                putExtra("ENTREPRISE_ID", contact.entrepriseNom)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateAppelList()
    }
}