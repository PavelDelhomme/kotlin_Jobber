package com.delhomme.jobber.Activity.Relance

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.R
import com.delhomme.jobber.Model.Relance
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsRelanceActivity : AppCompatActivity() {

    private lateinit var dataRepository: DataRepository
    private var relanceId: String? = null
    private lateinit var relance: Relance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_relance)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        dataRepository = DataRepository(this)
        relanceId = intent.getStringExtra("RELANCE_ID")

        if (relanceId == null) {
            finish()
            return
        }

        relance = dataRepository.getRelanceById(relanceId!!) ?: run {
            finish()
            return
        }

        displayRelanceDetails()

        findViewById<Button>(R.id.btnModifyRelance).setOnClickListener {
            val intent = Intent(this, EditRelanceActivity::class.java).apply {
                putExtra("RELANCE_ID", relance.id)
                putExtra("ENTREPRISE_ID", relance.entrepriseNom)
                putExtra("CANDIDATURE_ID", relance.candidatureId)
            }
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnEditRelance).setOnClickListener {
            val intent = Intent(this, EditRelanceActivity::class.java).apply {
                putExtra("RELANCE_ID", relance.id)
                putExtra("ENTREPRISE_ID", relance.entrepriseNom)
                putExtra("CANDIDATURE_ID", relance.candidatureId)
            }
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnDeleteRelance).setOnClickListener {
            DataRepository(applicationContext).deleteRelance(relance.id)
            val intentCandidature = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentCandidature)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
