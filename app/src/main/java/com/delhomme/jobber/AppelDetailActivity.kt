package com.delhomme.jobber

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class AppelDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appel_detail)

        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }
        val appelId = intent.getStringExtra("APPEL_ID") ?: return

        val dataRepository = DataRepository(this)
        val appel = dataRepository.getAppelById(appelId) ?: return

        val appelDate = findViewById<TextView>(R.id.appelDate)
        val appelNomContact = findViewById<TextView>(R.id.appelContactNom)
        val appelNomEntreprise = findViewById<TextView>(R.id.appelEntrepriseNom)
        val appelObjet = findViewById<TextView>(R.id.appelObjet)
        val appelNotes = findViewById<TextView>(R.id.appelNotes)

        val appelContactNom = appel.contact_id?.let { dataRepository.getContactById(it) }
        val appelEntrepriseNom = dataRepository.getEntrepriseById(appel.entreprise_id)

        appelNomContact.text = appelContactNom?.getFullName() ?: "No Contact"
        appelNomEntreprise.text = appelEntrepriseNom?.nom ?: "No Entreprise"
        appelDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(appel.date_appel)
        appelObjet.text = appel.objet
        appelNotes.text = appel.notes

        setTitle("Appel pour ${appelObjet.text} du ${appelDate.text} avec ${appelNomContact.text}")
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