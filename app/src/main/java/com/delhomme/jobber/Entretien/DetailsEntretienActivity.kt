package com.delhomme.jobber.Entretien

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsEntretienActivity : AppCompatActivity() {

    private lateinit var dataRepository: DataRepository
    private var entretienId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_entretien)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataRepository = DataRepository(this)
        entretienId = intent.getStringExtra("ENTRETIEN_ID")

        entretienId?.let {
            val entretien = dataRepository.getEntretienById(it)
            entretien?.let { bindData(it) }
        }
        findViewById<ImageButton>(R.id.btnEditEntretien).setOnClickListener {
            val intent = Intent(this, EditEntretienActivity::class.java)

            intent.putExtra("ENTRETIEN_ID", entretienId)

            startActivity(intent)
        }

    }

    private fun bindData(entretien: Entretien) {
        val entrepriseNom = dataRepository.getEntrepriseByNom(entretien.entrepriseNom)
        val candidatureOffre = dataRepository.getCandidatureById(entretien.candidature_id)

        findViewById<TextView>(R.id.tvEntretienDate).text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(entretien.date_entretien)
        findViewById<TextView>(R.id.tvEntretienEntreprise).text = entrepriseNom?.nom
        findViewById<TextView>(R.id.tvEntretienCandidature).text = candidatureOffre?.titre_offre
        findViewById<TextView>(R.id.tvEntretienType).text = entretien.type
        findViewById<TextView>(R.id.tvEntretienMode).text = entretien.mode
        findViewById<TextView>(R.id.tvEntretienNotes).text = entretien.notes_pre_entretien ?: "Aucune note"
        findViewById<TextView>(R.id.tvEntretienNotesPost).text = entretien.notes_post_entretien ?: "Aucune note"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
