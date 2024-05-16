package com.delhomme.jobber.Relance

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
import com.delhomme.jobber.R
import com.delhomme.jobber.Relance.adapter.RelanceAdapter
import com.delhomme.jobber.Relance.model.Relance

class FragmentRelances : Fragment() {
    private lateinit var adapter: RelanceAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_relances, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = RelanceAdapter(dataRepository.loadRelances(),dataRepository, this::onRelanceClicked, this::onDeleteRelanceClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btnAddRelance).setOnClickListener {
            startActivity(Intent(activity, AddRelanceActivity::class.java))
        }
    }

    private fun onRelanceClicked(relance: Relance) {
        val intent = Intent(activity, DetailsRelanceActivity::class.java).apply {
            putExtra("RELANCE_ID", relance.id)
        }
        startActivity(intent)
    }

    private fun onDeleteRelanceClicked(relanceId: String) {
        dataRepository.deleteRelance(relanceId)
        adapter.updateRelances(dataRepository.loadRelances())
    }
    override fun onResume() {
        super.onResume()
        adapter.updateRelances(dataRepository.loadRelances())
    }
}
