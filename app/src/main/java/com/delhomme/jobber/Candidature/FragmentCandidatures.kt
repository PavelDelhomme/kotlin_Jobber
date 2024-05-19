package com.delhomme.jobber.Candidature

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Candidature.adapter.CandidatureAdapter
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R

class FragmentCandidatures : Fragment() {
    private lateinit var adapter: CandidatureAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            adapter.updateCandidatures(dataRepository.getCandidatures())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_candidatures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = CandidatureAdapter(dataRepository.getCandidatures(),dataRepository, this::onCandidatureClicked, this::onDeleteCandidatureClicked, this::onEditCandidatureClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btnAddCandidature).setOnClickListener {
            startActivity(Intent(activity, AddCandidatureActivity::class.java))
        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(updateReceiver, IntentFilter("com.jobber.CANDIDATURE_LIST_UPDATED"))
    }

    private fun onCandidatureClicked(candidature: Candidature) {
        val intent = Intent(activity, DetailsCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidature.id)
        }
        startActivity(intent)
    }

    private fun onDeleteCandidatureClicked(candidatureId: String) {
        dataRepository.deleteCandidature(candidatureId)
        adapter.updateCandidatures(dataRepository.getCandidatures())
    }

    private fun onEditCandidatureClicked(candidatureId: String) {
        val intent = Intent(activity, EditCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidatureId)
        }
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        adapter.updateCandidatures(dataRepository.getCandidatures())
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updateReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updateReceiver)
    }

}
