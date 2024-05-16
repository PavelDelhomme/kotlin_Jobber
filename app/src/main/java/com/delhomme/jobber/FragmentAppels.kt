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
import com.delhomme.jobber.adapter.AppelAdapter
import com.delhomme.jobber.models.Appel

class FragmentAppels : Fragment() {
    private lateinit var adapter: AppelAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvAppels)
        adapter = AppelAdapter(dataRepository.loadAppels(), this::onAppelClicked, this::onDeleteAppelClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btnAddAppel).setOnClickListener {
            startActivity(Intent(activity, AddAppelActivity::class.java))
        }
    }

    private fun onAppelClicked(appel: Appel) {
        val intent = Intent(activity, DetailsAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appel.id)
        }
        startActivity(intent)
    }

    private fun onDeleteAppelClicked(appelId: String) {
        dataRepository.deleteAppel(appelId)
        adapter.updateAppels(dataRepository.loadAppels())
    }

    override fun onResume() {
        super.onResume()
        adapter.updateAppels(dataRepository.loadAppels())
    }
}
