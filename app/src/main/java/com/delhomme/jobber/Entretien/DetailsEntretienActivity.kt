package com.delhomme.jobber.Entretien

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsEntretienActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_entretiens)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val entretienId = intent.getStringExtra("ENTRETIEN_ID") ?: return
        val dataRepository = DataRepository(this)
        val entretien = dataRepository.getEntretienById(entretienId) ?: return
        val entrepriseNom = dataRepository.getEntrepriseById(entretien.entreprise_id)
        val candidatureOffre = dataRepository.getCandidatureById(entretien.candidature_id!!)

        findViewById<TextView>(R.id.tvEntretienDate).text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(entretien.date_entretien)
        findViewById<TextView>(R.id.tvEntretienEntreprise).text = entrepriseNom?.nom
        findViewById<TextView>(R.id.tvEntretienCandidature).text = candidatureOffre?.titre_offre
        findViewById<TextView>(R.id.tvEntretienType).text = entretien.type
        findViewById<TextView>(R.id.tvEntretienMode).text = entretien.mode
        findViewById<TextView>(R.id.tvEntretienNotes).text = entretien.notes_pre_entretien ?: "Aucune note"

        findViewById<Button>(R.id.btnReturn).setOnClickListener {
            finish()
        }
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
}