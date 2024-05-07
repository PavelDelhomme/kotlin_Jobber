package com.delhomme.jobber.CandidaturePacket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Candidature
import com.google.gson.Gson

class CandidatureListFragment : Fragment() {
    private val candidatures = mutableListOf<Candidature>()
    private lateinit var candidatureAdapter: CandidatureAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_candidature_list, container, false)

        val candidatureRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerCandidatures)
        candidatureRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemDecoration = androidx.recyclerview.widget.DividerItemDecoration(
            requireContext(), DividerItemDecoration.VERTICAL
        )

        candidatureRecyclerView.addItemDecoration(itemDecoration)

        candidatureAdapter = CandidatureAdapter(candidatures) { candidature ->
            showCandidatureDetails(candidature)
        }
        candidatureRecyclerView.adapter = candidatureAdapter

        loadCandidatures()

        return view
    }

    private fun showCandidatureDetails(candidature: Candidature) {
        val intent = Intent(requireContext(), CandidatureDetailsActivity::class.java)
        intent.putExtra("candidature_id", candidature.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadCandidatures()
    }

    private fun loadCandidatures() {
        Log.d("CandidatureListFragment", "loadCandidatures called")
        val sharedPreferences = requireContext().getSharedPreferences("candidatures_prefs", 0)
        val gson = Gson()

        candidatures.clear()

        for ((key, value) in sharedPreferences.all) {
            if (key.startsWith("candidature_")) {
                val candidatureJson = value as String
                val candidature = gson.fromJson(candidatureJson, Candidature::class.java)
                candidatures.add(candidature)
                Log.d("Listes des Candidatures ", "candidature : $candidature")
                Log.d("Listes candidatures ", "candidature.offre : ${candidature.titreOffre}")
            }
        }
        candidatures.sortByDescending { it.date }
        candidatureAdapter.notifyDataSetChanged()
    }
}