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
import com.delhomme.jobber.Activity.Candidature.AddCandidatureActivity
import com.delhomme.jobber.Activity.Candidature.DetailsCandidatureActivity
import com.delhomme.jobber.Activity.Candidature.EditCandidatureActivity
import com.delhomme.jobber.Adapter.CandidatureAdapter
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Candidature.SwipeCallback
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FragmentCandidatures : Fragment() {
    private lateinit var candidatureAdapter: CandidatureAdapter
    private lateinit var candidatureDataRepository: CandidatureDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_candidatures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Candidatures"

        candidatureDataRepository = CandidatureDataRepository(requireContext())
        initUI(view)
    }

    private fun initUI(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val emptyView = view.findViewById<TextView>(R.id.empty_view_candidatures)

        candidatureAdapter = CandidatureAdapter(
            candidatureDataRepository.getItems(),
            candidatureDataRepository,
            entrepriseDataRepository,
            this::onCandidatureClicked,
            this::onDeleteCandidatureClicked,
            this::onEditCandidatureClicked
        )
        recyclerView.adapter = candidatureAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        updateUI(recyclerView, emptyView)

        view.findViewById<Button>(R.id.btnAddCandidature).setOnClickListener {
            startActivity(Intent(activity, AddCandidatureActivity::class.java))
        }

        setupSwipeCallback(recyclerView)
    }


    private fun setupSwipeCallback(recyclerView: RecyclerView) {
        val swipeCallback = SwipeCallback(requireActivity(),
            { position -> showDeleteConfirmationDialog(candidatureAdapter.candidatures[position].id, position) },
            { position -> onEditCandidatureClicked(candidatureAdapter.candidatures[position].id) }
        )
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }

    private fun updateUI(recyclerView: RecyclerView, emptyView: TextView) {
        if (candidatureAdapter.itemCount > 0) {
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
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Supprimer") { _, _ -> onDeleteCandidatureClicked(candidatureId) }
            .show()
    }

    private fun onCandidatureClicked(candidature: Candidature) {
        startActivity(Intent(activity, DetailsCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidature.id)
        })
    }

    private fun onDeleteCandidatureClicked(candidatureId: String) {
        candidatureDataRepository.deleteCandidature(candidatureId)
        candidatureAdapter.updateCandidatures(candidatureDataRepository.getItems())
        updateUI(view?.findViewById(R.id.recyclerView) as RecyclerView, view?.findViewById(R.id.empty_view_candidatures) as TextView)
    }

    private fun onEditCandidatureClicked(candidatureId: String) {
        startActivity(Intent(activity, EditCandidatureActivity::class.java).apply {
            putExtra("CANDIDATURE_ID", candidatureId)
        })
    }
}
