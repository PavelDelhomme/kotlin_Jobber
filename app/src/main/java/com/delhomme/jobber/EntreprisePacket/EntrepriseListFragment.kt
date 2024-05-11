package com.delhomme.jobber.EntreprisePacket

import android.annotation.SuppressLint
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
    private val entreprises = mutableListOf<Entreprise>()
    private lateinit var entrepriseAdapter: EntrepriseAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entreprise_list, container, false)

        val entrepriseRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerEntreprises)
        entrepriseRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        entrepriseAdapter = EntrepriseAdapter(entreprises)
        entrepriseRecyclerView.adapter = entrepriseAdapter
        loadEntreprises()
        return view
    }

    fun loadEntreprises() {
        val sharedPreferences = requireContext().getSharedPreferences("entreprises_prefs", 0)
        val gson = Gson()
        val allEntries = sharedPreferences.all

        entreprises.clear()
        for ((_, value) in allEntries) {
            val entrepriseJson = value as String
            val entreprise = gson.fromJson(entrepriseJson, Entreprise::class.java)
            entreprises.add(entreprise)
        }
        entrepriseAdapter.notifyDataSetChanged()
    }
}
