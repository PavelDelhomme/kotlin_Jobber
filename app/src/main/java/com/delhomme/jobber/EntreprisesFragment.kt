package com.delhomme.jobber

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.EntrepriseAdapter

class EntreprisesFragment : Fragment() {

    private lateinit var dataRepository: DataRepository
    private lateinit var entreprisesAdapter: EntrepriseAdapter
    private lateinit var receiver: BroadcastReceiver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_entreprises, container, false)
        setupBroadcastReceiver()
        dataRepository = DataRepository(requireContext())
        entreprisesAdapter = EntrepriseAdapter(dataRepository.loadEntreprises()) {
            // Ici, vous pouvez gérer le clic sur un élément pour ouvrir l'activité de détail
            val intent = Intent(context, EntrepriseDetailActivity::class.java)
            intent.putExtra("ENTREPRISE_ID", it.id)
            startActivity(intent)
        }

        view.findViewById<RecyclerView>(R.id.rvEntreprises).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = entreprisesAdapter
        }

        return view
    }
    private fun setupBroadcastReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                entreprisesAdapter.updateEntreprises(dataRepository.loadEntreprises())
            }
        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, IntentFilter("com.delhomme.jobber.UPDATE_ENTREPRISES"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
    }
}
