package com.delhomme.jobber.Candidature

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddCandidatureActivity : AppCompatActivity() {
    private lateinit var dataRepository: DataRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_candidature)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataRepository = DataRepository(applicationContext)
        setupSpinners()
        setupDatePicker()

        findViewById<Button>(R.id.button_add_candidature).setOnClickListener {
            addCandidature()
        }
    }

    private fun setupSpinners() {
        /*
        val typePosteAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.type_poste_options,
            android.R.layout.simple_spinner_item
        )
        typePosteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.spinner_type_poste).adapter = typePosteAdapter

        val plateformeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.plateforme_options,
            android.R.layout.simple_spinner_item
        )
        plateformeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.spinner_plateforme).adapter = plateformeAdapter*/
        val typePosteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dataRepository.getTypePosteOptions())
        findViewById<Spinner>(R.id.spinner_type_poste).adapter = typePosteAdapter

        val plateformeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dataRepository.getPlateformeOptions())
        findViewById<Spinner>(R.id.spinner_plateforme).adapter = plateformeAdapter
    }

    private fun setupDatePicker() {
        val editTextDate = findViewById<EditText>(R.id.editText_date_candidature)
        editTextDate.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                editTextDate.setText(dateFormat.format(selectedDate.time))
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))

            datePicker.show()
        }
    }

    private fun addCandidature() {
        Log.d("AddCandidatureActivity", "Attempting to add a new candidature")
        // TODO ici le code pour ajouter une candidaure
        val titreOffre = findViewById<EditText>(R.id.editText_titre_offre).text.toString()
        val nomEntreprise = findViewById<EditText>(R.id.editText_nom_entreprise).text.toString()
        val plateformeUtilisee = findViewById<Spinner>(R.id.spinner_plateforme).selectedItem.toString()
        val typePoste = findViewById<Spinner>(R.id.spinner_type_poste).selectedItem.toString()
        val dateCandidature = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(findViewById<EditText>(R.id.editText_date_candidature).text.toString())!!
        val notesCandidature = findViewById<EditText>(R.id.editText_notes).text.toString()
        val lieuPoste = findViewById<EditText>(R.id.editText_lieuPoste).text.toString()

        val entreprise = DataRepository(this).getOrCreateEntreprise(nomEntreprise)

        // Vérification de l'existence de la candidature
        if (dataRepository.getCandidatures().any { it.titre_offre == titreOffre && it.entrepriseId == entreprise.id }) {
            Toast.makeText(this, "Cette candidature existe déjà !", Toast.LENGTH_SHORT).show()
            return
        }

        val newcandidature = Candidature(
            id = UUID.randomUUID().toString(),
            titre_offre = titreOffre,
            entrepriseId = entreprise.id,
            date_candidature = dateCandidature,
            plateforme = plateformeUtilisee,
            type_poste = typePoste,
            lieuPoste = lieuPoste,
            etat = "Candidaté et en attente",
            notes = notesCandidature
        )

        dataRepository.saveCandidature(newcandidature)
        //LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("com.delhomme.jobber.UPDATE_ENTREPRISES"))
        Toast.makeText(this, "Candidature ajoutée avec succès", Toast.LENGTH_SHORT).show()
        finish()
    }
}