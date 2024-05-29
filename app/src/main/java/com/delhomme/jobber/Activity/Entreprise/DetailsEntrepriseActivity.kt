package com.delhomme.jobber.Activity.Entreprise

import com.delhomme.jobber.Adapter.RelanceAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.delhomme.jobber.Adapter.AppelAdapter
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Activity.Candidature.DetailsCandidatureActivity
import com.delhomme.jobber.Activity.Candidature.EditCandidatureActivity
import com.delhomme.jobber.Adapter.CandidatureAdapter
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Activity.Contact.AddContactActivity
import com.delhomme.jobber.Activity.Contact.DetailsContactActivity
import com.delhomme.jobber.Activity.Contact.EditContactActivity
import com.delhomme.jobber.Adapter.ContactAdapter
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.Model.Entreprise
import com.delhomme.jobber.Activity.Entretien.AddEntretienActivity
import com.delhomme.jobber.Activity.Entretien.DetailsEntretienActivity
import com.delhomme.jobber.Activity.Entretien.EditEntretienActivity
import com.delhomme.jobber.Adapter.EntretienAdapter
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.R
import com.delhomme.jobber.Activity.Relance.AddRelanceActivity
import com.delhomme.jobber.Activity.Relance.DetailsRelanceActivity
import com.delhomme.jobber.Activity.Relance.EditRelanceActivity
import com.delhomme.jobber.Api.Repository.AppelDataRepository
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Api.Repository.RelanceDataRepository
import com.delhomme.jobber.Model.Relance

class DetailsEntrepriseActivity : AppCompatActivity() {
    private lateinit var candidatureDataRepository: CandidatureDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var appelDataRepository: AppelDataRepository
    private lateinit var entretienDataRepository: EntretienDataRepository
    private lateinit var relanceDataRepository: RelanceDataRepository

    private var entrepriseId: String? = null
    private lateinit var entreprise: Entreprise

    private lateinit var contactsAdapter: ContactAdapter
    private lateinit var appelsAdapter: AppelAdapter
    private lateinit var candidaturesAdapter: CandidatureAdapter
    private lateinit var relancesAdapter: RelanceAdapter
    private lateinit var entretiensAdapter: EntretienAdapter

    private lateinit var contactsEntreprise: List<Contact>
    private lateinit var candidaturesEntreprise: List<Candidature>
    private lateinit var relancesEntreprise: List<Relance>
    private lateinit var appelsEntreprise: List<Appel>
    private lateinit var entretiensEntreprise: List<Entretien>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_entreprise)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val entrepriseNom = intent.getStringExtra("ENTREPRISE_ID") ?: return
        initRepositories()

        entrepriseDataRepository = EntrepriseDataRepository(this)
        contactDataRepository = ContactDataRepository(this)
        candidatureDataRepository = CandidatureDataRepository(this)
        entretienDataRepository = EntretienDataRepository(this)
        relanceDataRepository = RelanceDataRepository(this)
        appelDataRepository = AppelDataRepository(this)

        entreprise = (entrepriseDataRepository.loadRelatedItemsById({ it }, entrepriseNom) ?: return) as Entreprise


        contactsEntreprise =
            listOf((entrepriseDataRepository.loadRelatedItemsById({ it }, entrepriseNom) ?: return) as Contact)
        candidaturesEntreprise =
            listOf((entrepriseDataRepository.loadRelatedItemsById({ it }, entrepriseNom) ?: return) as Candidature)
        relancesEntreprise =
            listOf((entrepriseDataRepository.loadRelatedItemsById({ it }, entrepriseNom) ?: return) as Relance)
        appelsEntreprise =
            listOf((entrepriseDataRepository.loadRelatedItemsById({ it }, entrepriseNom) ?: return) as Appel)
        entretiensEntreprise =
            listOf((entrepriseDataRepository.loadRelatedItemsById({ it }, entrepriseNom) ?: return) as Entretien)

        contactsAdapter = ContactAdapter(
            contactsEntreprise,
            contactDataRepository,
            this::onContactClicked,
            this::onDeleteContactClicked,
            this::onEditContactClicked
        )

        contactsAdapter = ContactAdapter(emptyList(), , this::onContactClicked, this::onDeleteContactClicked, this::onEditContactClicked)
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


    private fun initRepositories() {
        candidatureDataRepository = CandidatureDataRepository(applicationContext)
        contactDataRepository = ContactDataRepository(applicationContext)
        appelDataRepository = AppelDataRepository(applicationContext)
        entretienDataRepository = EntretienDataRepository(applicationContext)
        entrepriseDataRepository = EntrepriseDataRepository(applicationContext)
        relanceDataRepository = RelanceDataRepository(applicationContext)

        entrepriseId = intent.getStringExtra("ENTREPRISE_ID")

        if (entrepriseDataRepository.getItems().find { it.nom == entrepriseId } == null) {
            Toast.makeText(this, "Entreprise non trouvée !", Toast.LENGTH_LONG).show()
            finish()
        } else {
            entreprise = entrepriseDataRepository.getItems().find { it.nom == entrepriseId } ?: return
        }
    }

    private fun setupUI() {
        displayEntrepriseDetails()
        setupButtons()
        setupRecyclerView()
    }

    private fun displayEntrepriseDetails() {

    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", entreprise.nom)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAddAppel).setOnClickListener {
            val intent = Intent(this, AddAppelActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", entreprise.nom)
            }
            startActivity(intent)
        }
        updateAppelsList()

        findViewById<Button>(R.id.btnAddRelance).setOnClickListener {
            val intent = Intent(this, AddRelanceActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", entreprise.nom)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAddEntretien).setOnClickListener {
            val intent = Intent(this, AddEntretienActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", entreprise.nom)
            }
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        setupContactRecyclerView()
        setupAppelRecyclerView()
        setupCandidatureRecyclerView()
        setupRelanceRecyclerView()
        setupEntretienRecyclerView()
    }
    //TODO
    private fun setupContactRecyclerView() {
        val contacts = contactDataRepository.loadContactsForEntreprise(entreprise.nom)
        val contactsAdapter = ContactAdapter(contacts, contactDataRepository, entrepriseDataRepository, this::onContactClicked, this::onDeleteContactClicked, this::onEditContactClicked)
        findViewById<RecyclerView>(R.id.rvContacts).apply {
            layoutManager = LinearLayoutManager(this@DetailsEntrepriseActivity)
            adapter = contactsAdapter
        }
    }
    //TODO
    private fun setupAppelRecyclerView() {
        val appels = appelDataRepository.loadAppelsForEntreprise(entreprise.nom)
        val appelsAdapter = AppelAdapter(appels, appelDataRepository, contactDataRepository, this::onAppelClicked, this::onDeleteAppelClicked, this::onEditAppelClicked)

        findViewById<RecyclerView>(R.id.rvAppels).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appelsAdapter
        }
    }
    //TODO
    private fun setupEntretienRecyclerView() {
        val entretiens = entretienDataRepository.loadEntretiensForEntreprise(entreprise.nom)
        val entretienAdapter = EntretienAdapter(entretiens, entretienDataRepository, entrepriseDataRepository, this::onEntretienClicked, this::onDeleteEntretienClicked, this::onEditEntretienClicked)

        findViewById<RecyclerView>(R.id.rvEntretiens).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = entretienAdapter
        }
    }
    //TODO
    private fun setupRelanceRecyclerView() {
        val relances = relanceDataRepository.loadRelancesForEntreprise(entreprise.nom)
        val relanceAdapter = RelanceAdapter(relances, relanceDataRepository, candidatureDataRepository, this::onRelanceClicked, this::onDeleteRelanceClicked, this::onEditRelanceClicked)

        findViewById<RecyclerView>(R.id.rvRelances).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = relanceAdapter
        }
    }
    //TODO
    private fun setupCandidatureRecyclerView() {
        val candidatures = candidatureDataRepository.findByCondition { it.entrepriseNom == entreprise.nom }
        val candidaturesAdapter = CandidatureAdapter(candidatures, candidatureDataRepository, entrepriseDataRepository, this::onCandidatureClicked, this::onDeleteCandidatureClicked, this::onEditCandidatureClicked)

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
        contactDataRepository.deleteContact(contactId)
        Log.d("DetailsEntrepriseActivity", "onDeleteContactClicked")
        Log.d("DetailsEntrepriseActivity", "contactId : $contactId")
        contactsAdapter.updateContacts(contactDataRepository.loadContactsForEntreprise(entreprise.nom))
        entrepriseDataRepository.getItems()
        Log.d("DetailsEntrepriseActivity", "contactsAdapter updated")
    }

    private fun onDeleteAppelClicked(appelId: String) {
        Log.d("onDeleteAppelClicked", "Delete appel clicked")
        appelDataRepository.deleteAppel(appelId)
        val updatedAppels = appelDataRepository.loadAppelsForEntreprise(entreprise.nom)
        appelsAdapter.updateAppels(updatedAppels)
    }

    private fun onDeleteCandidatureClicked(candidatureId: String) {
        candidatureDataRepository.deleteCandidature(candidatureId)
        updateCandidaturesList()
    }
    private fun onDeleteRelanceClicked(relanceId: String) {
        relanceDataRepository.deleteRelance(relanceId)
        updateRelancesList()
    }

    private fun onDeleteEntretienClicked(entretienId: String) {
        entretienDataRepository.deleteEntretien(entretienId)
        updateEntretiensList()
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
        val candidatures = dataRepository.loadCandidaturesForEntreprise(entreprise.nom)
        if (candidatures.isNotEmpty()) {
            candidaturesAdapter.updateCandidatures(candidatures)
        } else {
            Log.d("DetailsEntrepriseActivity", "No candidature found for this entreprise.")
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
