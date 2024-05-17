package com.delhomme.jobber.Appel

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Appel.adapter.AppelAdapter
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R

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
        adapter = AppelAdapter(dataRepository.getAppels(),dataRepository, this::onAppelClicked, this::onDeleteAppelClicked, this::onEditAppelClicked)
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
        adapter.updateAppels(dataRepository.getAppels())
    }

    private fun onEditAppelClicked(appelId: String) {
        val intent = Intent(activity, EditAppelActivity::class.java).apply {
            putExtra("APPEL_ID", appelId)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateAppels(dataRepository.getAppels())
    }
}
