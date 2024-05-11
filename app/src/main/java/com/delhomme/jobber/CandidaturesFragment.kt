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
import com.delhomme.jobber.adapter.CandidatureAdapter

class CandidaturesFragment : Fragment() {
    private lateinit var adapter: CandidatureAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_candidatures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = CandidatureAdapter(dataRepository.loadCandidatures())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btnAddCandidature).setOnClickListener {
            startActivity(Intent(activity, AddCandidatureActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateList(dataRepository.loadCandidatures())
    }

}
