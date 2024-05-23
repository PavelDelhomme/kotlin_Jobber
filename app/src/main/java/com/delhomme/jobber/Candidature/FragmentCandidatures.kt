package com.delhomme.jobber.Candidature

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.delhomme.jobber.Candidature.adapter.CandidatureAdapter
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FragmentCandidatures : Fragment() {
    private lateinit var adapter: CandidatureAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("FragmentCandidatures", "Broadcast received: com.jobber.CANDIDATURE_LIST_UPDATED")
            adapter.updateCandidatures(dataRepository.getCandidatures())
            adapter.notifyDataSetChanged()
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
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val emptyView = view.findViewById<TextView>(R.id.empty_view_candidatures)

        (activity as AppCompatActivity).supportActionBar?.title = "Candidatures"

        adapter = CandidatureAdapter(
            dataRepository.getCandidatures(),
            dataRepository,
            this::onCandidatureClicked,
            this::onDeleteCandidatureClicked,
            this::onEditCandidatureClicked
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        updateUI(recyclerView, emptyView)

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            updateCandidatures()
            swipeRefreshLayout.isRefreshing = false
        }

        view.findViewById<Button>(R.id.btnAddCandidature).setOnClickListener {
            startActivity(Intent(activity, AddCandidatureActivity::class.java))
        }


        val swipeCallback = SwipeCallback(requireContext(),
            { position ->
                val candidature = adapter.candidatures[position]
                showDeleteConfirmationDialog(candidature.id, position)
            },
            { position ->
                val candidature = adapter.candidatures[position]
                onEditCandidatureClicked(candidature.id)
            }
        )

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(updateReceiver, IntentFilter("com.jobber.CANDIDATURE_LIST_UPDATED"))
    }

    private fun updateUI(recyclerView: RecyclerView, emptyView: TextView) {
        if (adapter.itemCount > 0) {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }

    private fun showDeleteConfirmationDialog(candidatureId: String, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmation de suppression")
            .setMessage("Voulez-vous vraiment supprimer cette candidature ?")
            .setNegativeButton("Annuler") { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setPositiveButton("Supprimer") { dialog, _ ->
                onDeleteCandidatureClicked(candidatureId)
                dialog.dismiss()
            }
            .show()
    }

    private fun onCandidatureClicked(candidature: Candidature) {
        val intent = Intent(activity, DetailsCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidature.id)
        }
        startActivity(intent)
    }

    private fun onDeleteCandidatureClicked(candidatureId: String) {
        dataRepository.deleteCandidature(candidatureId)
    }

    private fun onEditCandidatureClicked(candidatureId: String) {
        val intent = Intent(activity, EditCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidatureId)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(updateReceiver, IntentFilter("com.jobber.CANDIDATURE_LIST_UPDATED"))
        adapter.updateCandidatures(dataRepository.getCandidatures())
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updateReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updateReceiver)
    }

    private fun updateCandidatures() {
        adapter.updateCandidatures(dataRepository.getCandidatures())
        adapter.notifyDataSetChanged()
    }
}
