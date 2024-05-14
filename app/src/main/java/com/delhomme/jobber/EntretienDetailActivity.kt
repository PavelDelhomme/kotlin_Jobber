package com.delhomme.jobber

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class EntretienDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entretien_detail)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val entretienId = intent.getStringExtra("ENTRETIEN_ID") ?: return
        val dataRepository = DataRepository(this)
        val entretien = dataRepository.getEntretienById(entretienId) ?: return

        findViewById<TextView>(R.id.tvEntretienDate).text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(entretien.date_entretien)
        findViewById<TextView>(R.id.tvEntretienEntreprise).text = entretien.entrepriseNom ?: "Inconnu"
        findViewById<TextView>(R.id.tvEntretienType).text = entretien.type_entretien
        findViewById<TextView>(R.id.tvEntretienStyle).text = entretien.style_entretien
        findViewById<TextView>(R.id.tvEntretienNotes).text = entretien.notes_pre_entretien ?: "Aucune note"

        findViewById<Button>(R.id.btnReturn).setOnClickListener {
            finish()
        }
    }
}