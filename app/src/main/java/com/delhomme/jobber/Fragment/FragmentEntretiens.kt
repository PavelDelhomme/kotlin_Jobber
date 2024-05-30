package com.delhomme.jobber.Fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
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
import com.delhomme.jobber.Activity.Entretien.AddEntretienActivity
import com.delhomme.jobber.Activity.Entretien.DetailsEntretienActivity
import com.delhomme.jobber.Activity.Entretien.EditEntretienActivity
import com.delhomme.jobber.Adapter.EntretienAdapter
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.R
import com.delhomme.jobber.Utils.SwipeCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FragmentEntretiens : Fragment() {
    private lateinit var entretienAdapter: EntretienAdapter
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var entretienDataRepository: EntretienDataRepository
    private lateinit var broadcastReceiver: BroadcastReceiver


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_entretiens, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Entretiens"

        initRepositories()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEntretiens)
        val emptyView = view.findViewById<TextView>(R.id.empty_view_entretiens)

        entretienAdapter = EntretienAdapter(
            entretienDataRepository.getItems(),
            entretienDataRepository,
            entrepriseDataRepository,
            this::onEntretienClicked,
            this::onDeleteEntretienClicked,
            this::onEditEntretienClicked
        )
        recyclerView.adapter = entretienAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        updateUI(recyclerView, emptyView)

        view.findViewById<Button>(R.id.btnAddEntretien).setOnClickListener {
            startActivity(Intent(activity, AddEntretienActivity::class.java))
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                entretienAdapter.updateEntretiens(entretienDataRepository.getItems())
            }
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("ENTRETIENS_UPDATED"))

        setupSwipeCallback(recyclerView)
    }

    private fun initRepositories() {
        entrepriseDataRepository = EntrepriseDataRepository(requireContext())
        entretienDataRepository = EntretienDataRepository(requireContext())
    }

    private fun setupSwipeCallback(recyclerView: RecyclerView) {
        val swipeCallback = SwipeCallback(requireContext(),
            { position -> showDeleteConfirmationDialog(entretienAdapter.entretiens[position].id, position) },
            { position -> onEditEntretienClicked(entretienAdapter.entretiens[position].id) }
        )
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun updateUI(recyclerView: RecyclerView, emptyView: TextView) {
        if (entretienAdapter.itemCount > 0) {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }

    private fun showDeleteConfirmationDialog(entretienId: String, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmation de suppression")
            .setMessage("Voulez-vous vraiment supprimer cet entretien ?")
            .setNegativeButton("Annuler") { dialog, _ ->
                entretienAdapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setPositiveButton("Supprimer") { dialog, _ ->
                onDeleteEntretienClicked(entretienId)
                dialog.dismiss()
            }
            .show()
    }

    private fun onEntretienClicked(entretien: Entretien) {
        startActivity(Intent(activity, DetailsEntretienActivity::class.java).putExtra("ENTRETIEN_ID", entretien.id))
    }

    private fun onDeleteEntretienClicked(entretienId: String) {
        entretienDataRepository.deleteEntretien(entretienId)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent("ENTRETIENS_UPDATED"))
    }

    private fun onEditEntretienClicked(entretienId: String) {
        startActivity(Intent(activity, EditEntretienActivity::class.java).putExtra("ENTRETIEN_ID", entretienId))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }
}
