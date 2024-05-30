package com.delhomme.jobber.Activity.Entreprise

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.R

class EditEntrepriseActivity : AppCompatActivity() {
    private lateinit var etNomEntreprise: EditText
    private lateinit var btnSaveEntrepriseChanges: Button
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private var entrepriseNom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_entreprise)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etNomEntreprise = findViewById(R.id.etNomEntreprise)
        btnSaveEntrepriseChanges = findViewById(R.id.btnSaveEntrepriseChanges)
        entrepriseDataRepository = EntrepriseDataRepository(this)

        entrepriseNom = intent.getStringExtra("ENTREPRISE_ID")

        entrepriseNom?.let {
            val entreprise = entrepriseDataRepository.findByCondition { it.nom == entrepriseNom }.firstOrNull()
            if (entreprise != null) {
                etNomEntreprise.setText(entreprise.nom)
            } else {
                Toast.makeText(this, "Entreprise non trouvée.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        btnSaveEntrepriseChanges.setOnClickListener {
            saveEntrepriseChanges()
        }
        findViewById<Button>(R.id.btnCancelEntrepriseChanges).setOnClickListener {
            finish()
        }
    }

    private fun saveEntrepriseChanges() {
        val newNomEntreprise = etNomEntreprise.text.toString()

        entrepriseNom?.let { oldNom ->
            val existingEntreprise = entrepriseDataRepository.findByCondition { it.nom == oldNom }.firstOrNull()
            existingEntreprise?.let {
                it.nom = newNomEntreprise
                // Mise à jour de l'entreprise en utilisant la méthode générique saveItem qui appliquera updateOrAddItem
                entrepriseDataRepository.saveItem(it)
                Toast.makeText(this, "Entreprise mise à jour avec succès.", Toast.LENGTH_SHORT).show()
                finish()
            } ?: run {
                // Gérer le cas où l'entreprise n'est pas trouvée
                Toast.makeText(this, "Entreprise non trouvée.", Toast.LENGTH_LONG).show()
                finish()
            }
        } ?: run {
            // Gérer le cas où entrepriseNom est null
            Toast.makeText(this, "ID d'entreprise manquant.", Toast.LENGTH_LONG).show()
            finish()
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
