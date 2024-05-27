package com.delhomme.jobber.Search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Appel.DetailsAppelActivity
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Calendrier.DetailsEvenementActivity
import com.delhomme.jobber.Model.Evenement
import com.delhomme.jobber.Candidature.DetailsCandidatureActivity
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Contact.DetailsContactActivity
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.Entreprise.DetailsEntrepriseActivity
import com.delhomme.jobber.Model.Entreprise
import com.delhomme.jobber.Entretien.DetailsEntretienActivity
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.R
import com.delhomme.jobber.Relance.DetailsRelanceActivity
import com.delhomme.jobber.Model.Relance


class SearchResultsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var results: List<Any>? = null
    private val dataRepository by lazy { DataRepository(requireContext()) }


    companion object {
        fun newInstance(results: List<Any>) = SearchResultsFragment().apply {
            arguments = Bundle().apply {
                putSerializable("results", ArrayList(results))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        results = arguments?.getSerializable("results") as List<Any>?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.searchResultsRecyclerView)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SearchResultsAdapter(results ?: emptyList(), dataRepository, this::onItemClicked)
    }

    private fun onItemClicked(item: Any) {
        when (item) {
            is Candidature -> showCandidatureDetails(item)
            is Contact -> showContactDetails(item)
            is Entreprise -> showEntrepriseDetails(item)
            is Entretien -> showEntretienDetails(item)
            is Appel -> showAppelDetails(item)
            is Evenement -> showEvenementDetails(item)
            is Relance -> showRelanceDetails(item)
        }
    }

    private fun showCandidatureDetails(candidature: Candidature) {
        val intent = Intent(activity, DetailsCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidature.id)
        }
        startActivity(intent)
    }
    private fun showContactDetails(contact: Contact) {
        val intent = Intent(activity, DetailsContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
        }
        startActivity(intent)
    }
    private fun showEntrepriseDetails(entreprise: Entreprise) {
        val intent = Intent(activity, DetailsEntrepriseActivity::class.java).apply {
            putExtra("ENTREPRISE_ID", entreprise.nom)
        }
        startActivity(intent)
    }
    private fun showEntretienDetails(entretien: Entretien) {
        val intent = Intent(activity, DetailsEntretienActivity::class.java).apply {
            putExtra("ENTRETIEN_ID", entretien.id)
        }
        startActivity(intent)
    }
    private fun showAppelDetails(appel: Appel) {
        val intent = Intent(activity, DetailsAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appel.id)
        }
        startActivity(intent)
    }
    private fun showEvenementDetails(evenement: Evenement) {
        val intent = Intent(activity, DetailsEvenementActivity::class.java).apply {
            putExtra("EVENEMENT_ID", evenement.id)
        }
        startActivity(intent)
    }
    private fun showRelanceDetails(relance: Relance) {
        val intent = Intent(activity, DetailsRelanceActivity::class.java).apply {
            putExtra("RELANCE_ID", relance.id)
        }
        startActivity(intent)
    }
}