package com.delhomme.jobber.CandidaturePacket

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.R
import com.google.gson.Gson

class CandidatureDetailsActivity : AppCompatActivity() {
    private lateinit var candidature: Candidature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidature_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Récupérer les vues du layout
        val txtTitreOffre = findViewById<TextView>(R.id.txtTitreOffre)
        val txtEntreprise = findViewById<TextView>(R.id.txtEntreprise)
        val txtDescription = findViewById<TextView>(R.id.txtDescription)
        val txtLieuPoste = findViewById<TextView>(R.id.txtLieuPoste)
        val txtDateCandidature = findViewById<TextView>(R.id.txtDateCandidature)

        val candidatureId = intent.getStringExtra("candidature_id")
        loadCandidature(candidatureId)

        // Remplir les TextViews avec les données de la candidature
        txtTitreOffre.text = candidature.titreOffre
        txtEntreprise.text = candidature.entrepriseNom
        txtDescription.text = candidature.description
        txtLieuPoste.text = candidature.lieuDuPoste
        txtDateCandidature.text = candidature.date
    }

    private fun loadCandidature(candidatureId: String?) {
        val sharedPreferences = getSharedPreferences("candidatures_prefs", MODE_PRIVATE)
        val gson = Gson()

        for ((key, value) in sharedPreferences.all) {
            if (key == "candidature_$candidatureId") {
                val candidatureJson = value as String
                candidature = gson.fromJson(candidatureJson, Candidature::class.java)
                break
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

