package com.delhomme.jobber.Entretien

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
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.Entretien.adapter.EntretienAdapter
import com.delhomme.jobber.Entretien.model.Entretien
import com.delhomme.jobber.R

class FragmentEntretiens : Fragment() {
    private lateinit var adapter: EntretienAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_entretiens, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEntretiens)
        adapter = EntretienAdapter(dataRepository.getEntretiens(), dataRepository, this::onEntretienClicked, this::onDeleteEntretienClicked, this::onEditEntretienClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btnAddEntretien).setOnClickListener {
            startActivity(Intent(activity, AddEntretienActivity::class.java))
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                adapter.updateEntretiens(dataRepository.getEntretiens())
            }
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("ENTRETIENS_UPDATED"))
    }

    private fun onEntretienClicked(entretien: Entretien) {
        val intent = Intent(activity, DetailsEntretienActivity::class.java).apply {
            putExtra("ENTRETIEN_ID", entretien.id)
        }
        startActivity(intent)
    }

    private fun onDeleteEntretienClicked(entretienId: String) {
        dataRepository.deleteEntretien(entretienId)
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(Intent("ENTRETIENS_UPDATED"))
    }

    private fun onEditEntretienClicked(entretienId: String) {
        val intent = Intent(activity, EditEntretienActivity::class.java).apply {
            putExtra("ENTRETIEN_ID", entretienId)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateEntretiens(dataRepository.getEntretiens())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(broadcastReceiver)
    }
}
