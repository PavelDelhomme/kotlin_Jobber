package com.delhomme.jobber.EntreprisePacket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R

class EntrepriseListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entreprise_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val entrepriseRecyclerView = findViewById<RecyclerView>(R.id.recyclerEntreprises)
        entrepriseRecyclerView.layoutManager = LinearLayoutManager(this)

        // Remplacez par une vraie liste récupérée
        val entreprises = listOf<Entreprise>()

        val adapter = EntrepriseAdapter(entreprises)
        entrepriseRecyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }
}