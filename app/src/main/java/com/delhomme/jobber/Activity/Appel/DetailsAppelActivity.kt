package com.delhomme.jobber.Activity.Appel

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.Repository.AppelDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsAppelActivity : AppCompatActivity() {
    private lateinit var appelDataRepository: AppelDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_appel)

        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }

        appelDataRepository = AppelDataRepository(this)
        contactDataRepository = ContactDataRepository(this)
        entrepriseDataRepository = EntrepriseDataRepository(this)

        val appelId = intent.getStringExtra("APPEL_ID") ?: return
        val appel = appelDataRepository.getAppelById(appelId) ?: return

        val appelDate = findViewById<TextView>(R.id.appelDate)
        val appelNomContact = findViewById<TextView>(R.id.appelContactNom)
        val appelNomEntreprise = findViewById<TextView>(R.id.appelEntrepriseNom)
        val appelObjet = findViewById<TextView>(R.id.appelObjet)
        val appelNotes = findViewById<TextView>(R.id.appelNotes)

        appelNomContact.text = appel.contact_id?.let { contactDataRepository.findByCondition { contact -> contact.id == it }.firstOrNull()?.getFullName() } ?: "No Contact"
        appelNomEntreprise.text = appel.entrepriseNom?.let { entrepriseDataRepository.findByCondition { entreprise -> entreprise.nom == it }.firstOrNull()?.nom } ?: "No Entreprise"
        appelDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(appel.date_appel)
        appelObjet.text = appel.objet
        appelNotes.text = appel.notes

        setTitle("${appelObjet.text}: ${appelDate.text} - ${appelNomContact.text}")
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