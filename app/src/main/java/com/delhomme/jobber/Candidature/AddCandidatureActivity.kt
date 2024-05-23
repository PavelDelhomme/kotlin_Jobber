package com.delhomme.jobber.Candidature

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.delhomme.jobber.Calendrier.Evenement
import com.delhomme.jobber.Calendrier.EventType
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.CandidatureState
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddCandidatureActivity : AppCompatActivity() {
    private lateinit var dataRepository: DataRepository
    private lateinit var etDateCandidature: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_candidature)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataRepository = DataRepository(applicationContext)
        etDateCandidature = findViewById(R.id.editText_date_candidature)
        setupSpinners()
        setupDatePicker()

        findViewById<Button>(R.id.button_add_candidature).setOnClickListener {
            addCandidature()
        }
    }

    private fun setupSpinners() {
        val typePosteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dataRepository.getTypePosteOptions())
        findViewById<Spinner>(R.id.spinner_type_poste).adapter = typePosteAdapter

        val plateformeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dataRepository.getPlateformeOptions())
        findViewById<Spinner>(R.id.spinner_plateforme).adapter = plateformeAdapter
    }

    private fun setupDatePicker() {
        etDateCandidature.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, day, hour, minute)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
                    etDateCandidature.setText(dateFormat.format(selectedDate.time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }
        Log.d("AddCandidatureActivityDate", "etDateCandidature setupDatePicker : $etDateCandidature")
    }

    private fun addCandidature() {
        Log.d("AddCandidatureActivityDate", "Attempting to add a new candidature")
        val titreOffre = findViewById<EditText>(R.id.editText_titre_offre).text.toString()
        val nomEntreprise = findViewById<EditText>(R.id.editText_nom_entreprise).text.toString()
        val plateformeUtilisee = findViewById<Spinner>(R.id.spinner_plateforme).selectedItem.toString()
        val typePoste = findViewById<Spinner>(R.id.spinner_type_poste).selectedItem.toString()
        val dateCandidature = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).parse(etDateCandidature.text.toString())!!
        val notesCandidature = findViewById<EditText>(R.id.editText_notes).text.toString()
        val lieuPoste = findViewById<EditText>(R.id.editText_lieuPoste).text.toString()

        val entreprise = dataRepository.getOrCreateEntreprise(nomEntreprise)

        if (dataRepository.getCandidatures().any { it.titre_offre == titreOffre && it.entrepriseNom == entreprise.nom }) {
            Toast.makeText(this, "Cette candidature existe déjà !", Toast.LENGTH_SHORT).show()
            return
        }

        val newCandidature = Candidature(
            id = UUID.randomUUID().toString(),
            titre_offre = titreOffre,
            entrepriseNom = entreprise.nom,
            date_candidature = dateCandidature,
            plateforme = plateformeUtilisee,
            type_poste = typePoste,
            lieuPoste = lieuPoste,
            state = CandidatureState.CANDIDATEE_ET_EN_ATTENTE,
            notes = notesCandidature,
        )

        dataRepository.saveCandidature(newCandidature)

        Log.d("AddCandidatureActivity", "newCandidature : $newCandidature\n newCandidature.date ${newCandidature.date_candidature}")
        Toast.makeText(this, "Candidature ajoutée avec succès", Toast.LENGTH_SHORT).show()

        val evenement = Evenement(
            id = UUID.randomUUID().toString(),
            title = "Candidature : $titreOffre",
            description = "Candidature pour $nomEntreprise à ${etDateCandidature.text}",
            startTime = dateCandidature.time,
            endTime = dateCandidature.time + 10 * 60 * 1000,
            type = EventType.Candidature,
            relatedId = newCandidature.id,
            entrepriseId = entreprise.nom
        )
        dataRepository.saveEvent(evenement)

        val intentEvent = Intent("com.jobber.EVENEMENT_LIST_UPDATED")


        val intentCandidature = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentCandidature)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentEvent)
        finish()
    }
}
