package com.delhomme.jobber.Activity.Entretien

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsEntretienActivity : AppCompatActivity() {

    private lateinit var entretienDataRepository: EntretienDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var candidatureDataRepository: CandidatureDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    private var entretienId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_entretien)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initRepositories()
        entretienId = intent.getStringExtra("ENTRETIEN_ID")


        entretienId?.let {
            val entretien = entretienDataRepository.findByCondition { it.id == entretienId }.firstOrNull()
            entretien?.let { bindData(it) }
        }
        findViewById<ImageButton>(R.id.btnEditEntretien).setOnClickListener {
            val intent = Intent(this, EditEntretienActivity::class.java)
            intent.putExtra("ENTRETIEN_ID", entretienId)
            startActivity(intent)
        }
    }

    private fun initRepositories() {
        entretienDataRepository = EntretienDataRepository(this)
        entrepriseDataRepository = EntrepriseDataRepository(this)
        candidatureDataRepository = CandidatureDataRepository(this)
        contactDataRepository = ContactDataRepository(this)
    }

    private fun bindData(entretien: Entretien) {
        val entreprise = entrepriseDataRepository.findByCondition { it.nom == entretien.entrepriseNom }.firstOrNull()
        val candidature = candidatureDataRepository.findByCondition { it.id == entretien.candidatureId }.firstOrNull()

        findViewById<TextView>(R.id.tvEntretienDate).text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(entretien.date_entretien)
        findViewById<TextView>(R.id.tvEntretienEntreprise).text = entreprise?.nom
        findViewById<TextView>(R.id.tvEntretienCandidature).text = candidature?.titre_offre
        findViewById<TextView>(R.id.tvEntretienType).text = entretien.type
        findViewById<TextView>(R.id.tvEntretienMode).text = entretien.mode
        findViewById<TextView>(R.id.tvEntretienNotes).text = entretien.notes_pre_entretien ?: "Aucune note"
        findViewById<TextView>(R.id.tvEntretienNotesPost).text = entretien.notes_post_entretien ?: "Aucune note post entretiein"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
