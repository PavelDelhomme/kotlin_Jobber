package com.delhomme.jobber

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.models.Candidature
import com.delhomme.jobber.models.Entreprise
import com.google.gson.Gson
import java.util.Calendar
import java.util.UUID

class CandidatureAddActivity : AppCompatActivity() {

    private lateinit var etDateCandidature: EditText
    private lateinit var etTitreOffre: EditText
    private lateinit var etEntreprise: EditText
    private lateinit var spTypeEmploi: Spinner
    private lateinit var spPlateforme: Spinner
    private lateinit var etLieuPoste: EditText
    private lateinit var etNotes: EditText
    private lateinit var submitButton: Button
    private lateinit var cbEstSpontannee: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidature_add)

        etDateCandidature = findViewById(R.id.etDateCandidature)
        etTitreOffre = findViewById(R.id.titreOffreEditText)
        etEntreprise = findViewById(R.id.entrepriseNomEditText)
        etLieuPoste = findViewById(R.id.lieuPosteEditText)
        spTypeEmploi = findViewById(R.id.typeEmploiSpinner)
        spPlateforme = findViewById(R.id.plateformeSpinner)
        etNotes = findViewById(R.id.notesEditText)
        cbEstSpontannee = findViewById(R.id.cbEstSpontannee)
        submitButton = findViewById(R.id.submitButton)

        setupDatePicker()
        setupSpinners()

        setTodayDateWithTime()

        submitButton.setOnClickListener { submitForm() }
    }


    private fun setupSpinners() {
        val typeEmploiAdapter = ArrayAdapter.createFromResource(
            this, R.array.type_emploi_options, android.R.layout.simple_spinner_item
        )
        typeEmploiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTypeEmploi.adapter = typeEmploiAdapter

        val plateformeAdapter = ArrayAdapter.createFromResource(
            this, R.array.plateforme_options, android.R.layout.simple_spinner_item
        )
        plateformeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPlateforme.adapter = plateformeAdapter
    }

    private fun submitForm() {
        val titreOffre = etTitreOffre.text.toString()
        val entrepriseNom = etEntreprise.text.toString()
        val dateCandidature = etDateCandidature.text.toString()
        val typeEmploi = spTypeEmploi.selectedItem.toString()
        val plateformeUtilisee = spPlateforme.selectedItem.toString()
        val lieuPoste = etLieuPoste.text.toString()
        val notes = etNotes.text.toString()
        val estSpontannee = cbEstSpontannee.isChecked

        if (titreOffre.isEmpty() || entrepriseNom.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show()
            return
        }

        // Rechercher ou créer l'entreprise
        val entreprise = findOrCreateEntreprise(entrepriseNom)

        // Générer un nouvel ID pour la candidature
        val candidatureId = generateId()

        // Créer la nouvelle candidature
        val candidature = Candidature(
            id = candidatureId,
            titreOffre = titreOffre,
            entrepriseNom = entreprise.nom,
            entrepriseInstance = entreprise,
            description = notes,
            date = dateCandidature,
            lieuDuPoste = lieuPoste,
            estSpontannee = estSpontannee,
            dateSuppression = null,
            estCorbeille = false,
            notes = notes,
            dateDuProchainEntretien = null,
            dateDuRetourEntretien = null,
            dateDerniereRelance = null,
            dateActuelleMoinsDateRetourEntretien = null,
            etatCandidature = "Candidaté et en attente",
            typeEmploi = typeEmploi,
            plateformeUtilisee = plateformeUtilisee,
            fichiersSupplementaires = listOf(),
            technologiesUtilisees = listOf(),
            savoirsFaires = listOf(),
            savoirsEtres = listOf(),
            relances = listOf(),
            entretiens = listOf()
        )

        // Enregistrer la candidature dans SharedPreferences
        saveCandidature(candidature)

        Toast.makeText(this, "Candidature ajoutée avec succès !", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun findOrCreateEntreprise(nom: String): Entreprise {
        // Vérifier si l'entreprise existe déjà dans SharedPreferences
        val sharedPreferences = getSharedPreferences("entreprises_prefs", MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        val gson = Gson()

        for ((_, value) in allEntries) {
            val entrepriseJson = value as String
            val entreprise = gson.fromJson(entrepriseJson, Entreprise::class.java)
            if (entreprise.nom.equals(nom, ignoreCase = true)) {
                return entreprise
            }
        }

        // Si l'entreprise n'existe pas, en créer une nouvelle
        val nouvelleEntreprise = Entreprise(
            id = generateId(),
            nom = nom,
            localisation = null,
            secteurActivite = null,
            description = null,
            email = null,
            siteEntreprise = null
        )

        // Sauvegarder la nouvelle entreprise dans SharedPreferences
        saveEntreprise(nouvelleEntreprise)

        return nouvelleEntreprise
    }


    private fun saveEntreprise(entreprise: Entreprise) {
        val sharedPreferences = getSharedPreferences("entreprises_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val entrepriseJson = gson.toJson(entreprise)
        editor.putString("entreprise_${entreprise.id}", entrepriseJson)
        editor.apply()
    }

    private fun saveCandidature(candidature: Candidature) {
        val sharedPreferences = getSharedPreferences("candidatures_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val candidatureJson = gson.toJson(candidature)
        editor.putString("candidature_${candidature.id}", candidatureJson)
        editor.apply()
        Log.d("saveCandidature", "La candidature à été enregistrer normalement ")
        Log.d("saveCandidature", "candidatureJson : $candidatureJson")
        Log.d("saveCandidature", "sharedPreferences.all : ${sharedPreferences.all}")
    }

    private fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    private fun setupDatePicker() {
        etDateCandidature.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)
                    etDateCandidature.setText(String.format("%d-%02d-%02d %02d:%02d", year, month + 1, dayOfMonth, hour, minute))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setTodayDateWithTime() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Remplir automatiquement le champ "Date de candidature" avec la date d'aujourd'hui et l'heure actuelle
        etDateCandidature.setText(String.format("%d-%02d-%02d %02d:%02d", year, month, day, hour, minute))

    }

}