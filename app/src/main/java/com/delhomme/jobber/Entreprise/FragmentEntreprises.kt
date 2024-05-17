package com.delhomme.jobber.Entreprise

import EntrepriseAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.Entreprise.model.Entreprise
import com.delhomme.jobber.R

class FragmentEntreprises : Fragment() {
    private lateinit var adapter: EntrepriseAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_entreprises, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvEntreprises)
        adapter = EntrepriseAdapter(dataRepository.getEntreprises(), this::onEntrepriseClicked, this::onDeleteEntrepriseClicked, this::onEditEntrepriseClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun onEntrepriseClicked(entreprise: Entreprise) {
        val intent = Intent(activity, DetailsEntrepriseActivity::class.java).apply {
            putExtra("ENTREPRISE_ID", entreprise.nom)
        }
        startActivity(intent)
    }

    private fun onDeleteEntrepriseClicked(entrepriseId: String) {
        dataRepository.deleteEntreprise(entrepriseId)
        adapter.updateEntreprises(dataRepository.getEntreprises())
    }

    private fun onEditEntrepriseClicked(entrepriseNom: String) {
        val intent = Intent(activity, EditEntrepriseActivity::class.java).apply {
            putExtra("ENTREPRISE_ID", entrepriseNom)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateEntreprises(dataRepository.getEntreprises())
    }
}
