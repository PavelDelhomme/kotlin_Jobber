package com.delhomme.jobber.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Activity.Relance.AddRelanceActivity
import com.delhomme.jobber.Activity.Relance.DetailsRelanceActivity
import com.delhomme.jobber.Activity.Relance.EditRelanceActivity
import com.delhomme.jobber.Adapter.RelanceAdapter
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.RelanceDataRepository
import com.delhomme.jobber.Candidature.SwipeCallback
import com.delhomme.jobber.Model.Relance
import com.delhomme.jobber.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FragmentRelances : Fragment() {
    private lateinit var relanceAdapter: RelanceAdapter
    private lateinit var relanceDataRepository: RelanceDataRepository
    private lateinit var candidatureDataRepository: CandidatureDataRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_relances, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Relances"

        relanceDataRepository = RelanceDataRepository(requireContext())
        candidatureDataRepository = CandidatureDataRepository(requireContext())
        initUI(view)
    }

    private fun initUI(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val emptyView = view.findViewById<TextView>(R.id.empty_view_relances)

        relanceAdapter = RelanceAdapter(
            relanceDataRepository.getItems(),
            relanceDataRepository,
            candidatureDataRepository,
            this::onRelanceClicked,
            this::onDeleteRelanceClicked,
            this::onEditRelanceClicked
        )
        recyclerView.adapter = relanceAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btnAddRelance).setOnClickListener {
            startActivity(Intent(activity, AddRelanceActivity::class.java))
        }

        setupSwipeCallback(recyclerView)
    }

    private fun setupSwipeCallback(recyclerView: RecyclerView) {
        val swipeCallback = SwipeCallback(requireContext(),
            { position -> showDeleteConfirmationDialog(relanceAdapter.relances[position].id, position) },
            { position -> onEditRelanceClicked(relanceAdapter.relances[position].id) }
        )
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }

    private fun showDeleteConfirmationDialog(relanceId: String, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmation de suppression")
            .setMessage("Voulez-vous vraiment supprimer cette relance ?")
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Supprimer") { _, _ -> onDeleteRelanceClicked(relanceId) }
            .show()
    }

    private fun onRelanceClicked(relance: Relance) {
        startActivity(Intent(activity, DetailsRelanceActivity::class.java).apply {
            putExtra("RELANCE_ID", relance.id)
        })
    }

    private fun onDeleteRelanceClicked(relanceId: String) {
        relanceDataRepository.deleteRelance(relanceId)
        relanceAdapter.updateRelances(relanceDataRepository.getItems())
    }

    private fun onEditRelanceClicked(relanceId: String) {
        startActivity(Intent(activity, EditRelanceActivity::class.java).apply {
            putExtra("RELANCE_ID", relanceId)
        })
    }

    override fun onResume() {
        super.onResume()
        relanceAdapter.updateRelances(relanceDataRepository.getItems())
    }
}
