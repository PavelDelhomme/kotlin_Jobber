package com.delhomme.jobber.EntreprisePacket

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.google.gson.Gson

class EntrepriseListFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entreprise_list, container, false)

        val entrepriseRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerEntreprises)
        entrepriseRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val entreprises = loadEntreprises(requireContext())

        val adapter = EntrepriseAdapter(entreprises)
        entrepriseRecyclerView.adapter = adapter

        return view
    }

    private fun loadEntreprises(context: Context): List<Entreprise> {
        val sharedPreferences = context.getSharedPreferences("entreprises_prefs",
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val allEntries = sharedPreferences.all
        val entreprises = mutableListOf<Entreprise>()

        for ((_, value) in allEntries) {
            val entrepriseJson = value as String
            val entreprise = gson.fromJson(entrepriseJson, Entreprise::class.java)
            entreprises.add(entreprise)
        }

        return entreprises
    }
}