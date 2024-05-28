package com.delhomme.jobber.Fragment

import com.delhomme.jobber.Adapter.RelanceAdapter
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
import com.delhomme.jobber.Activity.Relance.AddRelanceActivity
import com.delhomme.jobber.Utils.SwipeCallback
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.R
import com.delhomme.jobber.Model.Relance
import com.delhomme.jobber.Activity.Relance.DetailsRelanceActivity
import com.delhomme.jobber.Activity.Relance.EditRelanceActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FragmentRelances : Fragment() {
    private lateinit var adapter: RelanceAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }
    private lateinit var broadcasReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_relances, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val emptyView = view.findViewById<TextView>(R.id.empty_view_relances)

        (activity as AppCompatActivity).supportActionBar?.title = "Relances"

        adapter = RelanceAdapter(dataRepository.getRelances(), dataRepository, this::onRelanceClicked, this::onDeleteRelanceClicked, this::onEditRelanceClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        updateUI(recyclerView, emptyView)

        view.findViewById<Button>(R.id.btnAddRelance).setOnClickListener {
            startActivity(Intent(activity, AddRelanceActivity::class.java))
        }

        broadcasReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                adapter.updateRelances(dataRepository.getRelances())
            }
        }

        val swipeCallback = SwipeCallback(requireContext(),
            {position ->
                val relance = adapter.relances[position]
                showDeleteConfirmationDialog(relance.id, position)
            },
            { position ->
                val relance = adapter.relances[position]
                onEditRelanceClicked(relance.id)
            }
        )

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcasReceiver, IntentFilter("RELANCES_UPDATED"))

    }


    private fun showDeleteConfirmationDialog(relanceId: String, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmation de suppression")
            .setMessage("Voulez-vous vraiment supprimer cette relance ?")
            .setNegativeButton("Annuler") { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setPositiveButton("Supprimer") { dialog, _ ->
                onDeleteRelanceClicked(relanceId)
                dialog.dismiss()
            }
            .show()

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
    private fun onRelanceClicked(relance: Relance) {
        val intent = Intent(activity, DetailsRelanceActivity::class.java).apply {
            putExtra("RELANCE_ID", relance.id)
        }
        startActivity(intent)
    }

    private fun onDeleteRelanceClicked(relanceId: String) {
        dataRepository.deleteRelance(relanceId)
        adapter.updateRelances(dataRepository.getRelances())
    }

    private fun onEditRelanceClicked(relanceId: String) {
        val intent = Intent(activity, EditRelanceActivity::class.java).apply {
            putExtra("RELANCE_ID", relanceId)
        }
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        adapter.updateRelances(dataRepository.getRelances())
    }
}
