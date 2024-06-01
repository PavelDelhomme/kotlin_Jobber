package com.delhomme.jobber.Activity.Entreprise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Activity.Appel.AddAppelActivity
import com.delhomme.jobber.Activity.Appel.DetailsAppelActivity
import com.delhomme.jobber.Activity.Appel.EditAppelActivity
import com.delhomme.jobber.Activity.Candidature.DetailsCandidatureActivity
import com.delhomme.jobber.Activity.Candidature.EditCandidatureActivity
import com.delhomme.jobber.Activity.Contact.AddContactActivity
import com.delhomme.jobber.Activity.Contact.DetailsContactActivity
import com.delhomme.jobber.Activity.Contact.EditContactActivity
import com.delhomme.jobber.Activity.Entretien.AddEntretienActivity
import com.delhomme.jobber.Activity.Entretien.DetailsEntretienActivity
import com.delhomme.jobber.Activity.Entretien.EditEntretienActivity
import com.delhomme.jobber.Activity.Relance.AddRelanceActivity
import com.delhomme.jobber.Activity.Relance.DetailsRelanceActivity
import com.delhomme.jobber.Activity.Relance.EditRelanceActivity
import com.delhomme.jobber.Adapter.AppelAdapter
import com.delhomme.jobber.Adapter.CandidatureAdapter
import com.delhomme.jobber.Adapter.ContactAdapter
import com.delhomme.jobber.Adapter.EntretienAdapter
import com.delhomme.jobber.Adapter.RelanceAdapter
import com.delhomme.jobber.Api.Repository.AppelDataRepository
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Api.Repository.RelanceDataRepository
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Model.Entreprise
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.Model.Relance
import com.delhomme.jobber.R

class DetailsEntrepriseActivity : AppCompatActivity() {
    private lateinit var candidatureDataRepository: CandidatureDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var appelDataRepository: AppelDataRepository
    private lateinit var entretienDataRepository: EntretienDataRepository
    private lateinit var relanceDataRepository: RelanceDataRepository

    private lateinit var entreprise: Entreprise
    private lateinit var contactsAdapter: ContactAdapter
    private lateinit var appelsAdapter: AppelAdapter
    private lateinit var candidaturesAdapter: CandidatureAdapter
    private lateinit var relancesAdapter: RelanceAdapter
    private lateinit var entretiensAdapter: EntretienAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_entreprise)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val entrepriseId = intent.getStringExtra("ENTREPRISE_ID") ?: return
        initRepositories()
        fetchAndDisplayEntrepriseDetails(entrepriseId)
        setupRecyclerView()
        setupButtons()
    }

    private fun initRepositories() {
        entrepriseDataRepository = EntrepriseDataRepository(applicationContext)
        contactDataRepository = ContactDataRepository(applicationContext)
        appelDataRepository = AppelDataRepository(applicationContext)
        entretienDataRepository = EntretienDataRepository(applicationContext)
        candidatureDataRepository = CandidatureDataRepository(applicationContext)
        relanceDataRepository = RelanceDataRepository(applicationContext)
    }

    private fun fetchAndDisplayEntrepriseDetails(entrepriseId: String) {
        entreprise = entrepriseDataRepository.findByCondition { it.nom == entrepriseId }.firstOrNull() ?: return
        findViewById<TextView>(R.id.tvEntrepriseName).text = entreprise.nom
    }

    private fun setupRecyclerView() {
        setupContactRecyclerView()
        setupAppelRecyclerView()
        setupCandidatureRecyclerView()
        setupRelanceRecyclerView()
        setupEntretienRecyclerView()
    }
    private fun setupContactRecyclerView() {
        val contacts = contactDataRepository.loadContactsForEntreprise(entreprise.nom)
        val contactsAdapter = ContactAdapter(contacts, contactDataRepository, entrepriseDataRepository, this::onContactClicked, this::onDeleteContactClicked, this::onEditContactClicked)
        findViewById<RecyclerView>(R.id.rvContacts).apply {
            layoutManager = LinearLayoutManager(this@DetailsEntrepriseActivity)
            adapter = contactsAdapter
        }
    }
    private fun setupAppelRecyclerView() {
        val appels = appelDataRepository.loadAppelsForEntreprise(entreprise.nom)
        val appelsAdapter = AppelAdapter(appels, appelDataRepository, contactDataRepository, this::onAppelClicked, this::onDeleteAppelClicked, this::onEditAppelClicked)
        findViewById<RecyclerView>(R.id.rvAppels).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appelsAdapter
        }
    }
    private fun setupEntretienRecyclerView() {
        val entretiens = entretienDataRepository.loadEntretiensForEntreprise(entreprise.nom)
        val entretienAdapter = EntretienAdapter(entretiens, entretienDataRepository, entrepriseDataRepository, this::onEntretienClicked, this::onDeleteEntretienClicked, this::onEditEntretienClicked)

        findViewById<RecyclerView>(R.id.rvEntretiens).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = entretienAdapter
        }
    }
    private fun setupRelanceRecyclerView() {
        val relances = relanceDataRepository.loadRelancesForEntreprise(entreprise.nom)
        val relanceAdapter = RelanceAdapter(relances, relanceDataRepository, candidatureDataRepository, this::onRelanceClicked, this::onDeleteRelanceClicked, this::onEditRelanceClicked)
        findViewById<RecyclerView>(R.id.rvRelances).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = relanceAdapter
        }
    }
    private fun setupCandidatureRecyclerView() {
        val candidatures = candidatureDataRepository.findByCondition { it.entreprise == entreprise.nom }
        val candidaturesAdapter = CandidatureAdapter(candidatures, candidatureDataRepository, entrepriseDataRepository, this::onCandidatureClicked, this::onDeleteCandidatureClicked, this::onEditCandidatureClicked)
        findViewById<RecyclerView>(R.id.rvCandidatures).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = candidaturesAdapter
        }
    }
    private fun setupButtons() {
        findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java).putExtra("ENTREPRISE_ID", entreprise.nom))
        }
        findViewById<Button>(R.id.btnAddAppel).setOnClickListener {
            startActivity(Intent(this, AddAppelActivity::class.java).putExtra("ENTREPRISE_ID", entreprise.nom))
        }
        findViewById<Button>(R.id.btnAddRelance).setOnClickListener {
            startActivity(Intent(this, AddRelanceActivity::class.java).putExtra("ENTREPRISE_ID", entreprise.nom))
        }
        findViewById<Button>(R.id.btnAddEntretien).setOnClickListener {
            startActivity(Intent(this, AddEntretienActivity::class.java).putExtra("ENTREPRISE_ID", entreprise.nom))
        }
    }
    private fun onContactClicked(contact: Contact) {
        startActivity(Intent(this, DetailsContactActivity::class.java).putExtra("CONTACT_ID", contact.id))
    }
    private fun onAppelClicked(appel: Appel) {
        startActivity(Intent(this, DetailsAppelActivity::class.java).putExtra("APPEL_ID", appel.id))
    }
    private fun onCandidatureClicked(candidature: Candidature) {
        startActivity(Intent(this, DetailsCandidatureActivity::class.java).putExtra("CANDIDATURE_ID", candidature.id))
    }
    private fun onRelanceClicked(relance: Relance) {
        startActivity(Intent(this, DetailsRelanceActivity::class.java).putExtra("RELANCE_ID", relance.id))
    }
    private fun onEntretienClicked(entretien: Entretien) {
        startActivity(Intent(this, DetailsEntretienActivity::class.java).putExtra("ENTRETIEN_ID", entretien.id))
    }

    private fun onDeleteContactClicked(contactId: String) {
        contactDataRepository.deleteContact(contactId)
        contactsAdapter.updateContacts(contactDataRepository.loadContactsForEntreprise(entreprise.nom))
    }
    private fun onDeleteAppelClicked(appelId: String) {
        appelDataRepository.deleteAppel(appelId)
        appelsAdapter.updateAppels(appelDataRepository.loadAppelsForEntreprise(entreprise.nom))
    }
    private fun onDeleteCandidatureClicked(candidatureId: String) {
        candidatureDataRepository.deleteCandidature(candidatureId)
        candidaturesAdapter.updateCandidatures(candidatureDataRepository.findByCondition { it.entreprise == entreprise.nom })
    }
    private fun onDeleteRelanceClicked(relanceId: String) {
        relanceDataRepository.deleteRelance(relanceId)
        relancesAdapter.updateRelances(relanceDataRepository.loadRelancesForEntreprise(entreprise.nom))
    }
    private fun onDeleteEntretienClicked(entretienId: String) {
        entretienDataRepository.deleteEntretien(entretienId)
        entretiensAdapter.updateEntretiens(entretienDataRepository.loadEntretiensForEntreprise(entreprise.nom))
    }

    private fun onEditContactClicked(contactId: String) {
        startActivity(Intent(this, EditContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contactId)
            putExtra("ENTREPRISE_ID", entreprise.nom)
        })
    }

    private fun onEditAppelClicked(appelId: String) {
        startActivity(Intent(this, EditAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appelId)
            putExtra("ENTREPRISE_ID", entreprise.nom)
        })
    }

    private fun updateContactList() {
        // TODO ici je suis sensé récupérer les contacts lié à l'entreprise
        val contacts = contactDataRepository.loadContactsForEntreprise(entreprise.nom)
        if (contacts.isNotEmpty()) {
            contactsAdapter.updateContacts(contacts)
        } else {
            Log.d("DetailsEntrepriseActivity", "No contacts found for this entreprise.")
        }
    }

    private fun updateCandidaturesList() {
        val candidatures = candidatureDataRepository.loadRelatedItemsById2({ it.entreprise }, entreprise.nom)
        if (candidatures.isNotEmpty()) {
            candidaturesAdapter.updateCandidatures(candidatures)
        } else {
            Log.d("DetailsEntrepriseActivity", "No candidatures found for thi entreprise.")
        }
    }

    private fun updateEntretiensList() {
        val entretiens = entretienDataRepository.loadEntretiensForEntreprise(entreprise.nom)
        if (entretiens.isNotEmpty()) {
            entretiensAdapter.updateEntretiens(entretiens)
        } else {
            Log.d("DetailsEntrepriseActivity", "No entretien found for this entreprise.")
        }
    }

    private fun updateRelancesList() {
        val relances = relanceDataRepository.loadRelancesForEntreprise(entreprise.nom)
        if (relances.isNotEmpty()) {
            relancesAdapter.updateRelances(relances)
        } else {
            Log.d("DetailsEntrepriseActivity", "No relances found for this entreprise.")
        }
    }

    private fun updateAppelsList() {
        val appels = appelDataRepository.loadAppelsForEntreprise(entreprise.nom)
        if (appels.isNotEmpty()) {
            appelsAdapter.updateAppels(appels)
        } else {
            Log.d("DetailsEntrepriseActivity", "No appels found for this entreprise.")
        }
    }

    private fun onEditEntretienClicked(entretienId: String) {
        val intent = Intent(this, EditEntretienActivity::class.java).apply {
            putExtra("ENTRETIEN_ID", entretienId)
            putExtra("ENTREPRISE_ID", entreprise.nom)
        }
        startActivity(intent)
    }
    private fun onEditRelanceClicked(relanceId: String) {
        val intent = Intent(this, EditRelanceActivity::class.java).apply {
            putExtra("RELANCE_ID", relanceId)
            putExtra("ENTREPRISE_ID", entreprise.nom)
        }
        startActivity(intent)
    }
    private fun onEditCandidatureClicked(candidatureId: String) {
        val intent = Intent(this, EditCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidatureId)
            putExtra("ENTREPRISE_ID", entreprise.nom)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        entrepriseDataRepository.reloadEntreprises()
        reloadAppelsAndContacts()
        reloadRelancesAndEntretiens()
        if (this::contactsAdapter.isInitialized) {
            updateContactList()
        }
        if (this::candidaturesAdapter.isInitialized) {
            updateCandidaturesList()
        }
        if (this::relancesAdapter.isInitialized) {
            updateRelancesList()
        }
        if (this::entretiensAdapter.isInitialized) {
            updateEntretiensList()
        }
        if (this::appelsAdapter.isInitialized) {
            updateAppelsList()
        }

    }

    private fun reloadAppelsAndContacts() {
        if (this::contactsAdapter.isInitialized && this::appelsAdapter.isInitialized) {
            val contacts = contactDataRepository.loadContactsForEntreprise(entreprise.nom)
            contactsAdapter.updateContacts(contacts)

            val appels = appelDataRepository.loadAppelsForEntreprise(entreprise.nom)
            appelsAdapter.updateAppels(appels)
        }
    }
    private fun reloadRelancesAndEntretiens() {
        if (this::relancesAdapter.isInitialized && this::entretiensAdapter.isInitialized) {
            val relances = relanceDataRepository.loadRelancesForEntreprise(entreprise.nom)
            relancesAdapter.updateRelances(relances)

            val entretiens = entretienDataRepository.loadEntretiensForEntreprise(entreprise.nom)
            entretiensAdapter.updateEntretiens(entretiens)
        }
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
