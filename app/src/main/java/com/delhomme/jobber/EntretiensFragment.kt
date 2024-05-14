package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.EntretienAdapter
import com.delhomme.jobber.models.Entretien

class EntretiensFragment : Fragment() {
    private lateinit var adapter: EntretienAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_entretiens, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEntretiens)
        adapter = EntretienAdapter(dataRepository.loadEntretiens(), this::onEntretienClicked, this::onDeleteEntretienClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btnAddEntretien).setOnClickListener {
            startActivity(Intent(activity, AddEntretienActivity::class.java))
        }
    }

    private fun onEntretienClicked(entretien: Entretien) {
        val intent = Intent(activity, EntretienDetailActivity::class.java).apply {
            putExtra("ENTRETIEN_ID", entretien.id)
        }
        startActivity(intent)
    }

    private fun onDeleteEntretienClicked(entretienId: String) {
        dataRepository.deleteEntretien(entretienId)
        adapter.updateEntretiens(dataRepository.loadEntretiens())
    }

    override fun onResume() {
        super.onResume()
        adapter.updateEntretiens(dataRepository.loadEntretiens())
    }
}