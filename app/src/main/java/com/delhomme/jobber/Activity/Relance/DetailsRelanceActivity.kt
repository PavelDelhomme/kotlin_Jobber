package com.delhomme.jobber.Activity.Relance

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Api.Repository.RelanceDataRepository
import com.delhomme.jobber.Model.Relance
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsRelanceActivity : AppCompatActivity() {
    private lateinit var relanceDataRepository: RelanceDataRepository
    private lateinit var relance: Relance
    private var relanceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_relance)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        relanceDataRepository = RelanceDataRepository(this)
        relanceId = intent.getStringExtra("RELANCE_ID") ?: return

        relance = relanceDataRepository.findByCondition { it.id == relanceId }.firstOrNull() ?: return

        displayRelanceDetails()

        findViewById<Button>(R.id.btnModifyRelance).setOnClickListener {
            navigateToEditRelance()
        }

        findViewById<ImageButton>(R.id.btnEditRelance).setOnClickListener {
            navigateToEditRelance()
        }

        findViewById<ImageButton>(R.id.btnDeleteRelance).setOnClickListener {
            relanceDataRepository.deleteRelance(relance.id)
            finish()
        }
    }
    private fun displayRelanceDetails() {
        findViewById<TextView>(R.id.tvDateRelance).text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(relance.date_relance)
        findViewById<TextView>(R.id.tvPlateforme).text = relance.plateformeUtilisee
        findViewById<TextView>(R.id.tvEntreprise).text = relance.entrepriseNom
        findViewById<TextView>(R.id.tvContact).text = relance.contactId ?: "Aucun contact"
        findViewById<TextView>(R.id.tvNotes).text = relance.notes ?: "Aucune note"
    }


    private fun navigateToEditRelance() {
        Intent(this, EditRelanceActivity::class.java).also {
            it.putExtra("RELANCE_ID", relance.id)
            it.putExtra("ENTREPRISE_ID", relance.entrepriseNom)
            it.putExtra("CANDIDATURE_ID", relance.candidatureId)
            startActivity(it)
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
