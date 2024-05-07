package com.delhomme.jobber

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.CandidatureAdapter
import com.delhomme.jobber.models.Candidature

class CandidatureListFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_candidature_list, container, false)

        val candidatureRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerCandidatures)
        candidatureRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val candidatures = listOf<Candidature>()

        val adapter = CandidatureAdapter(candidatures)
        candidatureRecyclerView.adapter = adapter

        return view
    }
}
