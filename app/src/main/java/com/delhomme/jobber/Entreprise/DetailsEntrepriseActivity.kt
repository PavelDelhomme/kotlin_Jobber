package com.delhomme.jobber.Entreprise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Appel.AddAppelActivity
import com.delhomme.jobber.Appel.DetailsAppelActivity
import com.delhomme.jobber.Appel.EditAppelActivity
import com.delhomme.jobber.Appel.adapter.AppelAdapter
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.Candidature.DetailsCandidatureActivity
import com.delhomme.jobber.Candidature.EditCandidatureActivity
import com.delhomme.jobber.Candidature.adapter.CandidatureAdapter
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.Contact.AddContactActivity
import com.delhomme.jobber.Contact.DetailsContactActivity
import com.delhomme.jobber.Contact.EditContactActivity
import com.delhomme.jobber.Contact.adapter.ContactAdapter
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.Entreprise.model.Entreprise
import com.delhomme.jobber.Entretien.AddEntretienActivity
import com.delhomme.jobber.Entretien.DetailsEntretienActivity
import com.delhomme.jobber.Entretien.EditEntretienActivity
import com.delhomme.jobber.Entretien.adapter.EntretienAdapter
import com.delhomme.jobber.Entretien.model.Entretien
import com.delhomme.jobber.R
import com.delhomme.jobber.Relance.AddRelanceActivity
import com.delhomme.jobber.Relance.DetailsRelanceActivity
import com.delhomme.jobber.Relance.EditRelanceActivity
import com.delhomme.jobber.Relance.adapter.RelanceAdapter
import com.delhomme.jobber.Relance.model.Relance

class DetailsEntrepriseActivity : AppCompatActivity() {

    private lateinit var dataRepository: DataRepository
    private lateinit var entreprise: Entreprise
    private lateinit var contactsAdapter: ContactAdapter
    private lateinit var appelsAdapter: AppelAdapter
    private lateinit var candidaturesAdapter: CandidatureAdapter
    private lateinit var relancesAdapter: RelanceAdapter
    private lateinit var entretiensAdapter: EntretienAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_entreprise)

        if(supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val entrepriseNom = intent.getStringExtra("ENTREPRISE_ID") ?: return
        dataRepository = DataRepository(this)
        entreprise = dataRepository.getEntrepriseByNom(entrepriseNom) ?: return

        contactsAdapter = ContactAdapter(emptyList(), dataRepository, this::onContactClicked, this::onDeleteContactClicked, this::onEditContactClicked)
        appelsAdapter = AppelAdapter(emptyList(), dataRepository, this::onAppelClicked, this::onDeleteAppelClicked, this::onEditAppelClicked)
        relancesAdapter = RelanceAdapter(emptyList(), dataRepository, this::onRelanceClicked, this::onDeleteRelanceClicked, this::onEditRelanceClicked)
        entretiensAdapter = EntretienAdapter(emptyList(),dataRepository, this::onEntretienClicked, this::onDeleteEntretienClicked, this::onEditEntretienClicked)

        findViewById<TextView>(R.id.tvEntrepriseName).text = entreprise.nom

        setupRecyclerView()
        setupAddContactButton()
        setupAddAppelButton()
        setupAddRelanceButton()
        setupAddEntretienButton()
    }

    private fun setupRecyclerView() {
        setupContactRecyclerView()
        setupAppelRecyclerView()
        setupCandidatureRecyclerView()
        setupRelanceRecyclerView()
        setupEntretienRecyclerView()
    }
    private fun setupContactRecyclerView() {
        val contacts = dataRepository.loadContactsForEntreprise(entreprise.nom)
        val contactsAdapter = ContactAdapter(contacts, dataRepository, this::onContactClicked, this::onDeleteContactClicked, this::onEditContactClicked)
        findViewById<RecyclerView>(R.id.rvContacts).apply {
            layoutManager = LinearLayoutManager(this@DetailsEntrepriseActivity)
            adapter = contactsAdapter
        }
    }

    private fun setupAppelRecyclerView() {
        val appels = dataRepository.loadAppelsForEntreprise(entreprise.nom)
        val appelsAdapter = AppelAdapter(appels, dataRepository, this::onAppelClicked, this::onDeleteAppelClicked, this::onEditAppelClicked)

        findViewById<RecyclerView>(R.id.rvAppels).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appelsAdapter
        }
    }

    private fun setupEntretienRecyclerView() {
        val entretiens = dataRepository.loadEntretiensForEntreprise(entreprise.nom)
        val entretienAdapter = EntretienAdapter(entretiens, dataRepository, this::onEntretienClicked, this::onDeleteEntretienClicked, this::onEditEntretienClicked)

        findViewById<RecyclerView>(R.id.rvEntretiens).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = entretienAdapter
        }
    }

    private fun setupRelanceRecyclerView() {
        val relances = dataRepository.loadRelancesForEntreprise(entreprise.nom)
        val relanceAdapter = RelanceAdapter(relances, dataRepository, this::onRelanceClicked, this::onDeleteRelanceClicked, this::onEditRelanceClicked)

        findViewById<RecyclerView>(R.id.rvRelances).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = relanceAdapter
        }
    }

    private fun setupCandidatureRecyclerView() {
        val candidatures = dataRepository.loadCandidaturesForEntreprise(entreprise.nom)
        val candidaturesAdapter = CandidatureAdapter(candidatures, dataRepository, this::onCandidatureClicked, this::onDeleteCandidatureClicked, this::onEditCandidatureClicked)

        findViewById<RecyclerView>(R.id.rvCandidatures).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = candidaturesAdapter
        }
    }
    private fun onContactClicked(contact: Contact) {
        val intent = Intent(this, DetailsContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
        }
        startActivity(intent)
    }

    private fun onAppelClicked(appel: Appel) {
        val intent = Intent(this, DetailsAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appel.id)
        }
        startActivity(intent)
    }

    private fun onCandidatureClicked(candidature: Candidature) {
        val intent = Intent(this, DetailsCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidature.id)
        }
        startActivity(intent)
    }

    private fun onRelanceClicked(relance: Relance) {
        val intent = Intent(this, DetailsRelanceActivity::class.java).apply {
            putExtra("RELANCE_ID", relance.id)
        }
        startActivity(intent)
    }

    private fun onEntretienClicked(entretien: Entretien) {
        val intent = Intent(this, DetailsEntretienActivity::class.java).apply {
            putExtra("ENTRETIEN_ID", entretien.id)
        }
        startActivity(intent)
    }

    private fun onDeleteContactClicked(contactId: String) {
        dataRepository.deleteContact(contactId)
        Log.d("DetailsEntrepriseActivity", "onDeleteContactClicked")
        Log.d("DetailsEntrepriseActivity", "contactId : $contactId")
        contactsAdapter.updateContacts(dataRepository.loadContactsForEntreprise(entreprise.nom))
        dataRepository.getEntreprises()
        Log.d("DetailsEntrepriseActivity", "contactsAdapter updated")
    }

    private fun onDeleteAppelClicked(appelId: String) {
        Log.d("onDeleteAppelClicked", "Delete appel clicked")
        dataRepository.deleteAppel(appelId)
        val updatedAppels = dataRepository.loadAppelsForEntreprise(entreprise.nom)
        appelsAdapter.updateAppels(updatedAppels)
    }

    private fun onDeleteCandidatureClicked(candidatureId: String) {
        dataRepository.deleteCandidature(candidatureId)
        updateCandidaturesList()
    }
    private fun onDeleteRelanceClicked(relanceId: String) {
        dataRepository.deleteRelance(relanceId)
        updateRelancesList()
    }

    private fun onDeleteEntretienClicked(entretienId: String) {
        dataRepository.deleteEntretien(entretienId)
        updateEntretiensList()
    }


    private fun updateContactList() {
        // TODO ici je suis sensé récupérer les contacts lié à l'entreprise
        val contacts = dataRepository.loadContactsForEntreprise(entreprise.nom)
        if (contacts.isNotEmpty()) {
            contactsAdapter.updateContacts(contacts)
        } else {
            Log.d("DetailsEntrepriseActivity", "No contacts found for this entreprise.")
        }
    }

    private fun updateCandidaturesList() {
        val candidatures = dataRepository.loadCandidaturesForEntreprise(entreprise.nom)
        if (candidatures.isNotEmpty()) {
            candidaturesAdapter.updateCandidatures(candidatures)
        } else {
            Log.d("DetailsEntrepriseActivity", "No candidature found for this entreprise.")
        }
    }

    private fun updateEntretiensList() {
        val entretiens = dataRepository.loadEntretiensForEntreprise(entreprise.nom)
        if (entretiens.isNotEmpty()) {
            entretiensAdapter.updateEntretiens(entretiens)
        } else {
            Log.d("DetailsEntrepriseActivity", "No entretien found for this entreprise.")
        }
    }

    private fun updateRelancesList() {
        val relances = dataRepository.loadRelancesForEntreprise(entreprise.nom)
        if (relances.isNotEmpty()) {
            relancesAdapter.updateRelances(relances)
        } else {
            Log.d("DetailsEntrepriseActivity", "No relances found for this entreprise.")
        }
    }

    private fun updateAppelsList() {
        val appels = dataRepository.loadAppelsForEntreprise(entreprise.nom)
        if (appels.isNotEmpty()) {
            appelsAdapter.updateAppels(appels)
        } else {
            Log.d("DetailsEntrepriseActivity", "No appels found for this entreprise.")
        }
    }

    private fun setupAddContactButton() {
        findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", entreprise.nom)
            }
            startActivity(intent)
        }
    }

    private fun setupAddAppelButton() {
        findViewById<Button>(R.id.btnAddAppel).setOnClickListener {
            val intent = Intent(this, AddAppelActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", entreprise.nom)
            }
            startActivity(intent)
        }
        updateAppelsList()
    }

    private fun setupAddRelanceButton() {
        findViewById<Button>(R.id.btnAddRelance).setOnClickListener {
            val intent = Intent(this, AddRelanceActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", entreprise.nom)
            }
            startActivity(intent)
        }
    }

    private fun setupAddEntretienButton() {
        findViewById<Button>(R.id.btnAddEntretien).setOnClickListener {
            val intent = Intent(this, AddEntretienActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", entreprise.nom)
            }
            startActivity(intent)
        }
    }
    private fun onEditContactClicked(contactId: String) {
        val intent = Intent(this, EditContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contactId)
            putExtra("ENTREPRISE_ID", entreprise.nom)
        }
        startActivity(intent)
    }

    private fun onEditAppelClicked(appelId: String) {
        val intent = Intent(this, EditAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appelId)
            putExtra("ENTREPRISE_ID", entreprise.nom)
        }
        startActivity(intent)
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
        dataRepository.reloadEntreprises()
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
            val contacts = dataRepository.loadContactsForEntreprise(entreprise.nom)
            contactsAdapter.updateContacts(contacts)

            val appels = dataRepository.loadAppelsForEntreprise(entreprise.nom)
            appelsAdapter.updateAppels(appels)
        }
    }
    private fun reloadRelancesAndEntretiens() {
        if (this::relancesAdapter.isInitialized && this::entretiensAdapter.isInitialized) {
            val relances = dataRepository.loadRelancesForEntreprise(entreprise.nom)
            relancesAdapter.updateRelances(relances)

            val entretiens = dataRepository.loadEntretiensForEntreprise(entreprise.nom)
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
