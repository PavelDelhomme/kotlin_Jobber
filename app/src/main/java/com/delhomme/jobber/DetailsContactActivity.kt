package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.AppelAdapter
import com.delhomme.jobber.models.Appel
import com.delhomme.jobber.models.Contact

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

        val contactName = findViewById<TextView>(R.id.contactName)
        val contactEmail = findViewById<TextView>(R.id.emailContact)
        val contactPhone = findViewById<TextView>(R.id.telephoneContact)
        val contactEntreprise = findViewById<TextView>(R.id.contactEntreprise)

        val entreprise = contact.entrepriseId?.let { dataRepository.getEntrepriseById(it)}

        contactName.text = contact.getFullName()
        contactEmail.text = contact.email
        contactPhone.text = contact.telephone
        contactEntreprise.text = entreprise?.nom ?: "Unknown"

        setTitle("Détails de ${contactName.text}")

        setupRecyclerView()
        setupAddAppelButton()
    }

    private fun setupRecyclerView() {
        setupAppelRecyclerView()
    }
    private fun setupAppelRecyclerView() {
        val appels = dataRepository.loadAppelsForContact(contact.id)
        appelAdapter = AppelAdapter(appels, this::onAppelClicked, this::onDeleteAppelClicked)

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
                putExtra("ENTREPRISE_ID", contact.entrepriseId)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateAppelList()
    }
}