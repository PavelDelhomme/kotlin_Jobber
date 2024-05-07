package com.delhomme.jobber.AppelPacket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Appel

class AppelListFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appel_list, container, false)

        val appelRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerAppels)
        appelRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val appels = listOf<Appel>()

        val adapter = AppelAdapter(appels)
        appelRecyclerView.adapter = adapter

        return view
    }
}