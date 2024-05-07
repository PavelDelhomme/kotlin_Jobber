package com.delhomme.jobber.EntretienPacket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R

class EntretienListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entretien_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val entretienRecyclerView = findViewById<RecyclerView>(R.id.recyclerEntretiens)
        entretienRecyclerView.layoutManager = LinearLayoutManager(this)

        // Remplacez par une vraie liste récupérée
        val entretiens = listOf<Entretien>()

        val adapter = EntretienAdapter(entretiens)
        entretienRecyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }
}