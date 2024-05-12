package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.models.Candidature

class CandidatureDetailActivity : AppCompatActivity() {


    private lateinit var dataRepository: DataRepository
    private lateinit var candidature: Candidature
    private lateinit var contactsAdapter: ArrayAdapter<String>


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

        setupListView()
        setupAddContactButton()
    }

    private fun setupListView() {
        val contactNames = ArrayList(candidature.entreprise.contacts?.map { it.nom }?.toList() ?: listOf())
        contactsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactNames)
        findViewById<ListView>(R.id.lvContacts).adapter = contactsAdapter
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
        val updatedContacts = ArrayList(candidature.entreprise.contacts?.map { it.nom } ?: listOf())
        contactsAdapter.clear()
        contactsAdapter.addAll(updatedContacts)
        contactsAdapter.notifyDataSetChanged()
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