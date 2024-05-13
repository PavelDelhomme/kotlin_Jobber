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
import com.delhomme.jobber.adapter.ContactAdapter
import com.delhomme.jobber.models.Candidature

class CandidatureDetailActivity : AppCompatActivity() {


    private lateinit var dataRepository: DataRepository
    private lateinit var candidature: Candidature
    private lateinit var contactsAdapter: ContactAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidature_detail)

        if(getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }

        val candidatureId = intent.getStringExtra("CANDIDATURE_ID") ?: return
        Log.d("CANDIDATURE_ID", "ID : ${candidatureId}")
        dataRepository = DataRepository(this)
        candidature = dataRepository.getCandidatureById(candidatureId) ?: return

        findViewById<TextView>(R.id.tvCandidatureInfo).text = "Candidature for ${candidature.titre_offre} at ${candidature.entreprise.nom}"

        setupAddContactButton()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val contacts = candidature.entreprise.contacts ?: listOf()
        Log.d("CandidatureDetailActivity", "Les contact de l'entreprise : ${candidature.entreprise.contacts ?: listOf()}")
        Log.d("CandidatureDetailActivity", "L'entreprise de la candidature : ${candidature.entreprise}")
        Log.d("CandidatureDetailActivity", "Nom de l'entreprise de la candidature : ${candidature.entreprise.nom}")
        contactsAdapter = ContactAdapter(contacts, this)  // Passez this si vous avez besoin du fragment pour quelque chose dans l'adapter
        findViewById<RecyclerView>(R.id.recyclerViewContacts).apply {
            layoutManager = LinearLayoutManager(this@CandidatureDetailActivity)
            adapter = contactsAdapter
        }
    }

    private fun setupAddContactButton() {
        findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            intent.putExtra("ENTREPRISE_ID", candidature.entreprise.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        dataRepository.reloadEntreprises()
        val updatedEntreprise = dataRepository.getEntrepriseById(candidature.entreprise.id)
        Log.d("DEBUG", "ID de l'entreprise de la candidature : ${candidature.entreprise.id}")
        updatedEntreprise?.let {
            Log.d("DEBUG", "Contacts de l'entreprise mise à jour : ${it.contacts}")
            contactsAdapter.updateContacts(it.contacts)
        } ?: Log.d("DEBUG", "Aucune entreprise trouvée avec cet ID.")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Termine l'activité quand le bouton retour est cliqué
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}