package com.delhomme.jobber.Activity.Contact

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Activity.Appel.AddAppelActivity
import com.delhomme.jobber.Activity.Appel.DetailsAppelActivity
import com.delhomme.jobber.Activity.Appel.EditAppelActivity
import com.delhomme.jobber.Activity.Candidature.DetailsCandidatureActivity
import com.delhomme.jobber.Activity.Candidature.EditCandidatureActivity
import com.delhomme.jobber.Activity.Entretien.DetailsEntretienActivity
import com.delhomme.jobber.Activity.Entretien.EditEntretienActivity
import com.delhomme.jobber.Adapter.AppelAdapter
import com.delhomme.jobber.Adapter.CandidatureAdapter
import com.delhomme.jobber.Adapter.EntretienAdapter
import com.delhomme.jobber.Api.Repository.AppelDataRepository
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.R

class DetailsContactActivity : AppCompatActivity() {
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var candidatureDataRepository: CandidatureDataRepository
    private lateinit var appelDataRepository: AppelDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var entretienDataRepository: EntretienDataRepository
    private lateinit var contact: Contact
    private lateinit var appelAdapter: AppelAdapter
    private lateinit var candidatureAdapter: CandidatureAdapter
    private lateinit var entretienAdapter: EntretienAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_contact)

        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }

        initRepositories()
        setupUI()
    }

    private fun initRepositories() {
        contactDataRepository = ContactDataRepository(this)
        appelDataRepository = AppelDataRepository(this)
        entrepriseDataRepository = EntrepriseDataRepository(this)
        candidatureDataRepository = CandidatureDataRepository(this)
        entretienDataRepository = EntretienDataRepository(this)

        val contactId = intent.getStringExtra("CONTACT_ID") ?: return

        if (contactDataRepository.getItems().find { it.id == contactId } == null) {
            Toast.makeText(this, "Contact non trouvé !", Toast.LENGTH_LONG).show()
            finish()
        } else {
            contact = contactDataRepository.getItems().find { it.id == contactId } ?: return
        }

    }

    private fun setupUI() {
        displayContactDetails()
        setupButtons()
        setupRecyclerView()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnAddAppel).setOnClickListener {
            startActivity(Intent(this, AddAppelActivity::class.java).apply {
                putExtra("CONTACT_ID", contact.id)
                putExtra("ENTREPRISE_ID", contact.entreprise)
            })
        }
    }

    private fun displayContactDetails() {
        val contactNomComplet = findViewById<TextView>(R.id.contactNomComplet)
        val contactEmail = findViewById<TextView>(R.id.emailContact)
        val contactPhone = findViewById<TextView>(R.id.telephoneContact)
        val contactEntreprise = findViewById<TextView>(R.id.contactEntreprise)

        val entreprise = entrepriseDataRepository.getItems().find { it.nom == contact.entreprise }

        contactNomComplet.text = contact.getFullName()
        contactEmail.text = contact.email
        contactPhone.text = contact.telephone
        contactEntreprise.text = entreprise?.nom ?: "Unknown"

        title = "Détails de ${contact.getFullName()}"

    }
    private fun setupRecyclerView() {
        setupAppelRecyclerView()
        setupCandidatureRecyclerView()
        setupEntretienRecyclerView()
    }
    private fun setupAppelRecyclerView() {
        val appels = appelDataRepository.loadAppelsForContact(contact.id)
        appelAdapter = AppelAdapter(appels, appelDataRepository, contactDataRepository, this::onAppelClicked, this::onDeleteAppelClicked, this::onEditAppelClicked)
        findViewById<RecyclerView>(R.id.recyclerViewAppels).apply {
            layoutManager = LinearLayoutManager(this@DetailsContactActivity)
            adapter = appelAdapter
        }
    }
    private fun setupCandidatureRecyclerView() {
        val candidatures = candidatureDataRepository.loadCandidaturesForContact(contact.id)
        candidatureAdapter = CandidatureAdapter(candidatures, candidatureDataRepository, entrepriseDataRepository, this::onCandidatureClicked, this::onDeleteCandidatureClicked, this::onEditCandidatureClicked)
        findViewById<RecyclerView>(R.id.recyclerViewAppels).apply {
            layoutManager = LinearLayoutManager(this@DetailsContactActivity)
            adapter = appelAdapter
        }
    }
    private fun setupEntretienRecyclerView() {
        val entretiens = entretienDataRepository.loadEntretiensForContact(contact.id)

        entretienAdapter = EntretienAdapter(entretiens, entretienDataRepository, entrepriseDataRepository, this::onEntretienClicked, this::onDeleteEntretienClicked, this::onEditEntretienClicked)
        findViewById<RecyclerView>(R.id.recyclerViewAppels).apply {
            layoutManager = LinearLayoutManager(this@DetailsContactActivity)
            adapter = appelAdapter
        }
    }

    private fun onCandidatureClicked(candidature: Candidature) {
        startActivity(Intent(this, DetailsCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidature.id)
        })
    }

    private fun onEntretienClicked(entretien: Entretien) {
        startActivity(Intent(this, DetailsEntretienActivity::class.java).apply {
            putExtra("ENTRETIEN_ID", entretien.id)
        })
    }

    private fun onAppelClicked(appel: Appel) {
        startActivity(Intent(this, DetailsAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appel.id)
        })
    }


    private fun updateAppelList() {
        val updatedAppels = appelDataRepository.loadAppelsForContact(contact.id)
        appelAdapter.updateAppels(updatedAppels)
    }
    private fun updateEntretienList() {
        val updatedEntretiens = entretienDataRepository.loadEntretiensForContact(contact.id)
        entretienAdapter.updateEntretiens(updatedEntretiens)
    }
    //TODO BONNE METHODE
    private fun updateCandidatureList() {
        val updatedCandidatures = candidatureDataRepository.loadCandidaturesForContact(contact.id)
        candidatureAdapter.updateCandidatures(updatedCandidatures)
    }
    private fun onDeleteAppelClicked(appelId: String) {
        appelDataRepository.deleteAppel(appelId)
        updateAppelList()
    }
    private fun onDeleteEntretienClicked(entretienId: String) {
        entretienDataRepository.deleteEntretien(entretienId)
        updateEntretienList()
    }

    private fun onDeleteCandidatureClicked(candidatureId: String) {
        candidatureDataRepository.deleteCandidature(candidatureId)
        updateCandidatureList()
    }

    private fun onEditAppelClicked(appelId: String) {
        startActivity(Intent(this, EditAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appelId)
            putExtra("CONTACT_ID", contact.id)
            putExtra("ENTREPRISE_ID", contact.entreprise)
        })
        updateAppelList()
    }

    private fun onEditEntretienClicked(entretienId: String) {
        startActivity(Intent(this, EditEntretienActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
            putExtra("ENTRETIEN_ID", entretienId)
        })
    }

    private fun onEditCandidatureClicked(candidatureId: String) {
        startActivity(Intent(this, EditCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidatureId)
        })
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

    override fun onResume() {
        super.onResume()
        updateAppelList()
    }
}