package com.delhomme.jobber.Candidature

import RelanceAdapter
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Appel.AddAppelActivity
import com.delhomme.jobber.Appel.DetailsAppelActivity
import com.delhomme.jobber.Appel.EditAppelActivity
import com.delhomme.jobber.Appel.adapter.AppelAdapter
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.CandidatureState
import com.delhomme.jobber.Contact.AddContactActivity
import com.delhomme.jobber.Contact.DetailsContactActivity
import com.delhomme.jobber.Contact.EditContactActivity
import com.delhomme.jobber.Contact.adapter.ContactAdapter
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.Entretien.AddEntretienActivity
import com.delhomme.jobber.Entretien.DetailsEntretienActivity
import com.delhomme.jobber.Entretien.EditEntretienActivity
import com.delhomme.jobber.Entretien.adapter.EntretienAdapter
import com.delhomme.jobber.Entretien.model.Entretien
import com.delhomme.jobber.R
import com.delhomme.jobber.Relance.AddRelanceActivity
import com.delhomme.jobber.Relance.DetailsRelanceActivity
import com.delhomme.jobber.Relance.EditRelanceActivity
import com.delhomme.jobber.Relance.model.Relance
import java.util.Locale

class DetailsCandidatureActivity : AppCompatActivity() {
    private lateinit var dataRepository: DataRepository
    private var candidatureId: String? = null
    private lateinit var candidature: Candidature

    private lateinit var contactAdapter: ContactAdapter
    private lateinit var appelAdapter: AppelAdapter
    private lateinit var entretienAdapter: EntretienAdapter
    private lateinit var relanceAdapter: RelanceAdapter

    private lateinit var spinnerState: Spinner
    private lateinit var stateMap: Map<String, CandidatureState>
    private lateinit var buttonConfirmChangeState: Button
    private lateinit var buttonEditCandidature: ImageButton
    companion object {
        private const val STATE_CHANGE_REQUEST = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_candidature)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataRepository = DataRepository(this)

        candidature = intent.getParcelableExtra<Candidature>("CANDIDATURE_KEY")!!

        if (candidature == null) {
            Toast.makeText(this, "Candidature non trouvée", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupRecyclerView()
        setupStateSpinner()
        displayCandidatureDetails()
        setupButtons()

        findViewById<ImageButton>(R.id.button_mark_as_rejected).setOnClickListener {
            markAsRejected()
        }
        findViewById<ImageButton>(R.id.button_mark_as_accepted).setOnClickListener {
            markAsAccepted()
        }

        findViewById<ImageButton>(R.id.btnEditCandidature).setOnClickListener {
            val intent = Intent(this, EditCandidatureActivity::class.java)

            intent.putExtra("CANDIDATURE_ID", candidatureId)

            startActivity(intent)
        }

        findViewById<Button>(R.id.buttonConfirmChangeState).setOnClickListener {
            showStateChangeDialog()
        }
        buttonConfirmChangeState.setOnClickListener {
            val selectedStateString = spinnerState.selectedItem.toString()
            try {
                val selectedState = CandidatureState.valueOf(selectedStateString.toUpperCase(Locale.ROOT).replace(" ", "_"))
                candidature.state = selectedState
                dataRepository.saveCandidature(candidature)
                LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("com.jobber.CANDIDATURE_LIST_UPDATED"))
                reloadCandidatureDetails()
                Toast.makeText(this, "État mis à jour", Toast.LENGTH_SHORT).show()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "Invalid state selected", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun displayCandidatureDetails() {
        val entrepriseNom = candidature.entrepriseNom
        val entreprise = dataRepository.getEntrepriseByNom(entrepriseNom)
        val entrepriseAffiche = entreprise?.nom ?: "Unknown Entreprise"
        findViewById<TextView>(R.id.titreoffre).text = candidature.titre_offre
        findViewById<TextView>(R.id.tvEntrepriseCandidature).text = entrepriseAffiche
        findViewById<TextView>(R.id.tvNotesCandidature).text = candidature.notes
        findViewById<TextView>(R.id.tvDateCandidature).text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(candidature.date_candidature)
        findViewById<TextView>(R.id.tvTypePoste).text = candidature.type_poste
        findViewById<TextView>(R.id.tvPlateforme).text = candidature.plateforme
        findViewById<TextView>(R.id.tvLieuPoste).text = candidature.lieuPoste

        val stateWithEmoji = when (candidature.state) {
            CandidatureState.CANDIDATEE_ET_EN_ATTENTE -> "🕒 Candidature en attente"
            CandidatureState.EN_ATTENTE_APRES_ENTRETIEN -> "🕒 En attente après entretien"
            CandidatureState.EN_ATTENTE_D_UN_ENTRETIEN -> "🕒 En attente d'un entretien"
            CandidatureState.FAIRE_UN_RETOUR_POST_ENTRETIEN -> "🔄 Faire un retour post entretien"
            CandidatureState.A_RELANCEE_APRES_ENTRETIEN -> "🔄 Relancée après entretien"
            CandidatureState.A_RELANCEE -> "🔄 À relancer"
            CandidatureState.RELANCEE_ET_EN_ATTENTE -> "🕒 Relancée et en attente"
            CandidatureState.AUCUNE_REPONSE -> "🚫 Aucune réponse"
            CandidatureState.NON_RETENU -> "❌ Non retenue"
            CandidatureState.ERREUR -> "⚠️ Erreur"
            CandidatureState.NON_RETENU_APRES_ENTRETIEN -> "❌️ Non retenue après entretien"
            CandidatureState.NON_RETENU_SANS_ENTRETIEN -> "❌ Non retenue"
            CandidatureState.ACCEPTEE -> "✅ Acceptée"
        }
        findViewById<TextView>(R.id.tvEtatCandidature).text = stateWithEmoji
    }

    private fun setupRecyclerView() {
        setupContactRecyclerView()
        setupAppelRecyclerView()
        setupEntretienRecyclerView()
        setupRelanceRecyclerView()
    }

    private fun setupButtons() {
        setupAddButtons()
        setupStateButtons()
    }
    private fun setupAddButtons() {
        setupAddContactButton()
        setupAddAppelButton()
        setupAddRelanceButton()
        setupAddEntretienButton()
    }

    private fun setupStateButtons() {
        spinnerState = findViewById(R.id.spinnerState)
        buttonConfirmChangeState = findViewById(R.id.buttonConfirmChangeState)

        val states = CandidatureState.entries.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, states)
        spinnerState.adapter = adapter

        buttonConfirmChangeState.setOnClickListener {
            val selectedState = CandidatureState.valueOf(spinnerState.selectedItem.toString())
            candidature.state = selectedState
            candidature.etatManuel = true
            dataRepository.saveCandidature(candidature)
            displayCandidatureDetails()
        }
        val intent = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    private fun setupStateSpinner() {
        stateMap = CandidatureState.values().associate {
            it.name.replace("_", " ").capitalize(Locale.ROOT) to it
        }

        spinnerState = findViewById(R.id.spinnerState)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, stateMap.keys.toList())
        spinnerState.adapter = adapter

        spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedStateName = parent.getItemAtPosition(position) as String
                val selectedState = stateMap[selectedStateName]
                selectedState?.let {
                    candidature.state = it
                    dataRepository.saveCandidature(candidature)
                    displayCandidatureDetails()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        val currentStateName = stateMap.entries.find { it.value ==  candidature.state }?.key
        currentStateName?.let {
            val currentIndex = stateMap.keys.toList().indexOf(it)
            spinnerState.setSelection(currentIndex)
        }
    }

    private fun setupContactRecyclerView() {
        val contacts = dataRepository.loadContactsForEntreprise(candidature.entrepriseNom)

        contactAdapter = ContactAdapter(
            contacts,
            dataRepository,
            this::onContactClicked,
            this::onDeleteContactClicked,
            this::onEditContactClicked
        )
        findViewById<RecyclerView>(R.id.recyclerViewContacts).apply {
            layoutManager = LinearLayoutManager(this@DetailsCandidatureActivity)
            adapter = contactAdapter
        }
    }

    private fun onContactClicked(contact: Contact) {
        val intent = Intent(this, DetailsContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
        }
        startActivity(intent)
    }

    private fun onDeleteContactClicked(contactId: String) {
        dataRepository.deleteContact(contactId)
        contactAdapter.updateContacts(dataRepository.loadContactsForEntreprise(candidature.entrepriseNom))
    }

    private fun onEditContactClicked(contactId: String) {
        val intent = Intent(this, EditContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contactId)
            putExtra("CANDIDATURE_ID", candidature.id)
        }
        startActivity(intent)
        updateContactList()
    }

    private fun updateContactList() {
        val newContacts = dataRepository.loadContactsForEntreprise(candidature.entrepriseNom)
        contactAdapter.updateContacts(newContacts)
    }

    private fun setupRelanceRecyclerView() {
        val relances = dataRepository.loadRelancesForCandidature(candidature.id)
        if (relances != null) {
            relanceAdapter = RelanceAdapter(
                relances,
                dataRepository,
                this::onRelanceClicked,
                this::onDeleteRelanceClicked,
                this::onEditRelanceClicked
            )
            findViewById<RecyclerView>(R.id.recyclerViewRelances).apply {
                layoutManager = LinearLayoutManager(this@DetailsCandidatureActivity)
                adapter = relanceAdapter
            }
            updateRelanceList()
        }
    }

    private fun onRelanceClicked(relance: Relance) {
        val intent = Intent(this, DetailsRelanceActivity::class.java).apply {
            putExtra("RELANCE_ID", relance.id)
        }
        startActivity(intent)
    }

    private fun onDeleteRelanceClicked(relanceId: String) {
        dataRepository.deleteRelance(relanceId)
        relanceAdapter.updateRelances(dataRepository.loadRelancesForCandidature(candidature.id))
    }

    private fun updateRelanceList() {
        val relances = dataRepository.loadRelancesForCandidature(candidature.id)
        if (relances.isNotEmpty()) {
            relanceAdapter.updateRelances(relances)
        } else {
            Log.d("DetailsCandidatureActivity", "No relances found for this candidature.")
        }
    }

    private fun setupAppelRecyclerView() {
        val appels = dataRepository.loadAppelsForCandidature(candidature.id)
        appelAdapter = AppelAdapter(appels, dataRepository, this::onAppelClicked, this::onDeleteAppelClicked, this::onEditAppelClicked)
        findViewById<RecyclerView>(R.id.recyclerViewAppels).apply {
            layoutManager = LinearLayoutManager(this@DetailsCandidatureActivity)
            adapter = appelAdapter
        }
    }

    private fun onAppelClicked(appel: Appel) {
        val intent = Intent(this, DetailsAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appel.id)
        }
        startActivity(intent)
    }

    private fun onDeleteAppelClicked(appelId: String) {
        dataRepository.deleteAppel(appelId)
        updateAppelList()
    }

    private fun updateAppelList() {
        val updatedAppels = dataRepository.loadAppelsForCandidature(candidature.id)
        Log.d("DetailsCandidatureActivity", "Appels of candidature : $updatedAppels")
        appelAdapter.updateAppels(updatedAppels)
    }

    private fun setupEntretienRecyclerView() {
        val entretiens = dataRepository.loadEntretiensForCandidature(candidature.id)
        entretienAdapter = EntretienAdapter(
            entretiens,
            dataRepository,
            this::onEntretienClicked,
            this::onDeleteEntretienClicked,
            this::onEditEntretienClicked
        )

        findViewById<RecyclerView>(R.id.recyclerViewEntretiens).apply {
            layoutManager = LinearLayoutManager(this@DetailsCandidatureActivity)
            adapter = entretienAdapter
        }
    }

    private fun onEntretienClicked(entretien: Entretien) {
        val intent = Intent(this, DetailsEntretienActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidature.id)
            putExtra("ENTRETIEN_ID", entretien.id)
        }
        startActivity(intent)
    }

    private fun onDeleteEntretienClicked(entretienId: String) {
        dataRepository.deleteEntretien(entretienId)
        updateEntretienList()
    }

    private fun onEditEntretienClicked(entretienId: String) {
        val intent = Intent(this, EditEntretienActivity::class.java).apply {
            putExtra("ENTRETIEN_ID", entretienId)
            putExtra("CANDIDATURE_ID", candidature.id)
        }
        startActivity(intent)
        updateEntretienList()
    }

    private fun onEditAppelClicked(appelId: String) {
        val intent = Intent(this, EditAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appelId)
            putExtra("CANDIDATURE_ID", candidature.id)
            putExtra("ENTREPRISE_ID", candidature.entrepriseNom)
        }
        startActivity(intent)
        updateAppelList()
    }

    private fun onEditRelanceClicked(relanceId: String) {
        val intent = Intent(this, EditRelanceActivity::class.java).apply {
            putExtra("ENTREPRISE_ID", candidature.entrepriseNom)
            putExtra("CANDIDATURE_ID", candidature.id)
            putExtra("RELANCE_ID", relanceId)
        }
        startActivity(intent)
        updateRelanceList()
    }

    private fun updateEntretienList() {
        entretienAdapter.updateEntretiens(dataRepository.loadEntretiensForCandidature(candidature.id))
    }

    private fun setupAddContactButton() {
        val btnAddContact = findViewById<Button>(R.id.btnAddContact)
        btnAddContact.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", candidature.entrepriseNom)
            }
            startActivity(intent)
        }
    }

    private fun setupAddAppelButton() {
        val btnAddAppel = findViewById<Button>(R.id.btnAddAppel)
        btnAddAppel.setOnClickListener {
            val intent = Intent(this, AddAppelActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", candidature.entrepriseNom)
                putExtra("CANDIDATURE_ID", candidature.id)
            }
            startActivity(intent)
        }
    }

    private fun setupAddRelanceButton() {
        val btnAddRelance = findViewById<Button>(R.id.btnAddRelance)
        btnAddRelance.setOnClickListener {
            val intent = Intent(this, AddRelanceActivity::class.java).apply {
                putExtra("ENTREPRISE_ID", candidature.entrepriseNom)
                putExtra("CANDIDATURE_ID", candidature.id)
            }
            startActivity(intent)
        }
    }

    private fun setupAddEntretienButton() {
        val btnAddEntretien = findViewById<Button>(R.id.btnAddEntretien)
        btnAddEntretien.setOnClickListener {
            val intent = Intent(this, AddEntretienActivity::class.java).apply {
                putExtra("CANDIDATURE_ID", candidature.id)
                putExtra("ENTREPRISE_ID", candidature.entrepriseNom)
            }
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()
        dataRepository.reloadEntreprises()
        updateEntretienList()
        updateContactList()
        updateAppelList()
        updateRelanceList()

        candidature = dataRepository.getCandidatureById(candidatureId!!) ?: return
        displayCandidatureDetails()
    }

    private fun markAsRejected() {
        candidature.etatManuel = true
        if (candidature.entretiens.isEmpty()) {
            candidature.state = CandidatureState.NON_RETENU_SANS_ENTRETIEN
        } else {
            candidature.state = CandidatureState.NON_RETENU_APRES_ENTRETIEN
        }
        dataRepository.saveCandidature(candidature)
        dataRepository.updateCandidatureState(candidature)

        val intent = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        finish()
    }

    private fun markAsAccepted() {
        candidature.state = CandidatureState.ACCEPTEE
        candidature.etatManuel = true
        dataRepository.saveCandidature(candidature)
        //dataRepository.updateCandidatureState(candidature)
        val intent = Intent("com.jobber.CANDIDATURE_LIST_UPDATED")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        displayCandidatureDetails()
        finish()
    }

    private fun loadCandidatureDetails(candidatureId: String) {
        candidature = dataRepository.getCandidatureById(candidatureId) ?: run {
            Toast.makeText(this, "Candidature non trouvée.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        displayCandidatureDetails()
    }
    private fun showStateChangeDialog() {
        val intent = Intent(this, StateChangeActivity::class.java)
        startActivityForResult(intent, STATE_CHANGE_REQUEST)
    }


    fun reloadCandidatureDetails() {
        candidature = dataRepository.getCandidatureById(candidatureId!!)!!
        if (candidature != null) {
            displayCandidatureDetails()
        }
    }
}
