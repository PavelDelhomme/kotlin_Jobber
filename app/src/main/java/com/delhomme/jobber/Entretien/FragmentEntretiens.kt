package com.delhomme.jobber.Entretien

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.Entretien.adapter.EntretienAdapter
import com.delhomme.jobber.Entretien.model.Entretien
import com.delhomme.jobber.R

class FragmentEntretiens : Fragment() {
    private lateinit var adapter: EntretienAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_entretiens, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEntretiens)
        adapter = EntretienAdapter(dataRepository.loadEntretiens(), dataRepository, this::onEntretienClicked, this::onDeleteEntretienClicked, this::onEditEntretienClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btnAddEntretien).setOnClickListener {
            startActivity(Intent(activity, AddEntretienActivity::class.java))
        }
    }

    private fun onEntretienClicked(entretien: Entretien) {
        val intent = Intent(activity, DetailsEntretienActivity::class.java).apply {
            putExtra("ENTRETIEN_ID", entretien.id)
        }
        startActivity(intent)
    }

    private fun onDeleteEntretienClicked(entretienId: String) {
        dataRepository.deleteEntretien(entretienId)
        adapter.updateEntretiens(dataRepository.loadEntretiens())
    }

    private fun onEditEntretienClicked(entretienId: String) {
        val intent = Intent(activity, EditEntretienActivity::class.java).apply {
            putExtra("ENTRETIEN_ID", entretienId)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateEntretiens(dataRepository.loadEntretiens())
    }
}