package com.delhomme.jobber

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.EntrepriseAdapter
import com.delhomme.jobber.models.Entreprise

class EntrepriseListFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entreprise_list, container, false)

        val entrepriseRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerEntreprises)
        entrepriseRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val entreprises = listOf<Entreprise>()

        val adapter = EntrepriseAdapter(entreprises)
        entrepriseRecyclerView.adapter = adapter

        return view
    }
}
