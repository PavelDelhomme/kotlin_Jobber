package com.delhomme.jobber.Entreprise

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R

class EditEntrepriseActivity : AppCompatActivity() {
    private lateinit var etNomEntreprise: EditText
    private lateinit var btnSaveEntrepriseChanges: Button
    private lateinit var dataRepository: DataRepository
    private var entrepriseNom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_entreprise)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etNomEntreprise = findViewById(R.id.etNomEntreprise)
        btnSaveEntrepriseChanges = findViewById(R.id.btnSaveEntrepriseChanges)
        dataRepository = DataRepository(this)

        entrepriseNom = intent.getStringExtra("ENTREPRISE_ID")

        entrepriseNom?.let {
            val entreprise = dataRepository.getEntrepriseByNom(it)
            etNomEntreprise.setText(entreprise?.nom)
        }

        btnSaveEntrepriseChanges.setOnClickListener {
            saveEntrepriseChanges()
        }
    }

    private fun saveEntrepriseChanges() {
        val newNomEntreprise = etNomEntreprise.text.toString()

        entrepriseNom?.let { oldNom ->
            val entreprise = dataRepository.getEntrepriseByNom(oldNom)
            entreprise?.let {
                dataRepository.editEntreprise(
                    entrepriseNom = oldNom,
                    newName = newNomEntreprise,
                    newContactIds = it.contactIds,
                    newRelancesIds = it.relanceIds,
                    newEntretiensIds = it.entretiens,
                    newCandidaturesIds = it.candidatureIds
                )
                dataRepository.updateEntrepriseName(oldNom, newNomEntreprise)
                Toast.makeText(this, "Entreprise mise à jour avec succès.", Toast.LENGTH_SHORT).show()
                dataRepository.reloadEntreprises()
                dataRepository.reloadEntretiens()
                dataRepository.reloadRelances()
                dataRepository.reloadContacts()
                dataRepository.reloadAppels()
                dataRepository.reloadCandidatures()
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
