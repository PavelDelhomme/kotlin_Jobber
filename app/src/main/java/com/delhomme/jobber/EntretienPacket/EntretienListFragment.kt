package com.delhomme.jobber.EntretienPacket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Entretien

class EntretienListFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entretien_list, container, false)

        val entretienRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerEntretiens)
        entretienRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val entretiens = listOf<Entretien>()

        val adapter = EntretienAdapter(entretiens)
        entretienRecyclerView.adapter = adapter

        return view
    }
}