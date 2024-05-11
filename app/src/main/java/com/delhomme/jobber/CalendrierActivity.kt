// CalendrierActivity.kt
package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.models.Evenement

class CalendrierActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendrier)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val evenementRecyclerView = findViewById<RecyclerView>(R.id.recyclerEvenements)
        evenementRecyclerView.layoutManager = LinearLayoutManager(this)

        // Remplacez par une vraie liste récupérée
        val evenements = listOf<Evenement>()

        val adapter = EvenementAdapter(evenements)
        evenementRecyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }
}

