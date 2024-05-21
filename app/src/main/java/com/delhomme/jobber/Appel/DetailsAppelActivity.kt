package com.delhomme.jobber.Appel

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsAppelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_appel)

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

        val appelContact = appel.contact_id?.let { dataRepository.getContactById(it) }
        val entreprise = appelContact?.entrepriseNom?.let { dataRepository.getEntrepriseByNom(it)}

        appelNomContact.text = appelContact?.getFullName() ?: "No Contact"
        appelNomEntreprise.text = entreprise?.nom ?: "No Entreprise"
        appelDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(appel.date_appel)
        appelObjet.text = appel.objet
        appelNotes.text = appel.notes

        setTitle("${appelObjet.text}:${appelDate.text} - ${appelNomContact.text}")
        Log.d("DetailsAppelActivityPerso", "")
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