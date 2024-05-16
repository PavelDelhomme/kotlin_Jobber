package com.delhomme.jobber.Candidature

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.util.Date

class EditCandidatureActivity : AppCompatActivity() {
    private lateinit var dataRepository: DataRepository
    private var candidatureId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_candidature)

        dataRepository = DataRepository(this)
        candidatureId = intent.getStringExtra("CANDIDATURE_ID")

        val candidature = candidatureId?.let { dataRepository.getCandidatureById(it) }
        if (candidature != null) {
            findViewById<EditText>(R.id.etTitreOffre).setText(candidature.titre_offre)
            findViewById<EditText>(R.id.etEtat).setText(candidature.etat)
            findViewById<EditText>(R.id.etNotes).setText(candidature.notes)
            findViewById<EditText>(R.id.etPlateforme).setText(candidature.plateforme)
            findViewById<EditText>(R.id.etTypePoste).setText(candidature.type_poste)
            findViewById<EditText>(R.id.etLieuPoste).setText(candidature.lieuPoste)
        }

        findViewById<Button>(R.id.btnSaveCandidatureChanges).setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val titre = findViewById<EditText>(R.id.etTitreOffre).text.toString()
        val etat = findViewById<EditText>(R.id.etEtat).text.toString()
        val notes = findViewById<EditText>(R.id.etNotes).text.toString()
        val plateforme = findViewById<EditText>(R.id.etPlateforme).text.toString()
        val typePoste = findViewById<EditText>(R.id.etTypePoste).text.toString()
        val lieuPoste = findViewById<EditText>(R.id.etLieuPoste).text.toString()

        val updatedCandidature = Candidature(
            id = candidatureId!!,
            titre_offre = titre,
            etat = etat,
            notes = notes,
            plateforme = plateforme,
            type_poste = typePoste,
            lieuPoste = lieuPoste,
            entrepriseId = "",
            date_candidature = Date()
        )

        dataRepository.saveCandidature(updatedCandidature)
        Toast.makeText(this, "Candidature Updated", Toast.LENGTH_SHORT).show()
        finish()
    }
}