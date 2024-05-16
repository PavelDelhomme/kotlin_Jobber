package com.delhomme.jobber.Entreprise

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import com.delhomme.jobber.Entreprise.adapter.EntrepriseAdapter
import com.delhomme.jobber.Entreprise.model.Entreprise

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
        adapter = EntrepriseAdapter(dataRepository.loadEntreprises(), this::onEntrepriseClicked, this::onDeleteEntrepriseClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun onEntrepriseClicked(entreprise: Entreprise) {
        val intent = Intent(activity, DetailsEntrepriseActivity::class.java).apply {
            putExtra("ENTREPRISE_ID", entreprise.id)
        }
        startActivity(intent)
    }

    private fun onDeleteEntrepriseClicked(entrepriseId: String) {
        dataRepository.deleteEntreprise(entrepriseId)
        adapter.updateEntreprises(dataRepository.loadEntreprises())
    }

    override fun onResume() {
        super.onResume()
        adapter.updateEntreprises(dataRepository.loadEntreprises())
    }
}
