package com.delhomme.jobber

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.ContactAdapter

class EntrepriseDetailActivity : AppCompatActivity() {

    private lateinit var dataRepository: DataRepository
    private lateinit var contactsAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entreprise_detail)

        val entrepriseId = intent.getStringExtra("ENTREPRISE_ID") ?: return
        dataRepository = DataRepository(this)
        val entreprise = dataRepository.getEntrepriseById(entrepriseId) ?: return

        val tvEntrepriseName = findViewById<TextView>(R.id.tvEntrepriseName)
        tvEntrepriseName.text = entreprise.nom

        contactsAdapter = ContactAdapter(entreprise.contacts, this)

        findViewById<RecyclerView>(R.id.rvContacts).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contactsAdapter
        }
    }
}
