package com.delhomme.jobber.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Activity.Entreprise.DetailsEntrepriseActivity
import com.delhomme.jobber.Activity.Entreprise.EditEntrepriseActivity
import com.delhomme.jobber.Adapter.EntrepriseAdapter
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Model.Entreprise
import com.delhomme.jobber.R
import com.delhomme.jobber.Utils.SwipeCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
class FragmentEntreprises : Fragment() {
    private lateinit var entrepriseAdapter: EntrepriseAdapter
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_entreprises, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Entreprises"

        initRepositories()

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvEntreprises)
        val emptyView = view.findViewById<TextView>(R.id.empty_view_entreprises)

        entrepriseAdapter = EntrepriseAdapter(entrepriseDataRepository.getItems(), this::onEntrepriseClicked, this::onDeleteEntrepriseClicked, this::onEditEntrepriseClicked)
        recyclerView.adapter = entrepriseAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        updateUI(recyclerView, emptyView)

        setupSwipeCallback(recyclerView)
    }

    private fun initRepositories() {
        entrepriseDataRepository = EntrepriseDataRepository(requireContext())
    }

    private fun setupSwipeCallback(recyclerView: RecyclerView) {
        val swipeCallback = SwipeCallback(requireContext(),
            { position -> showDeleteConfirmationDialog(entrepriseAdapter.entreprises[position].nom, position) },
            { position -> onEditEntrepriseClicked(entrepriseAdapter.entreprises[position].nom) }
        )
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun updateUI(recyclerView: RecyclerView, emptyView: TextView) {
        if (entrepriseAdapter.itemCount > 0) {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }

    private fun showDeleteConfirmationDialog(entrepriseNom: String, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmation de suppression")
            .setMessage("Voulez-vous vraiment supprimer cette entreprise ?")
            .setNegativeButton("Annuler") { dialog, _ ->
                entrepriseAdapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setPositiveButton("Supprimer") { dialog, _ ->
                onDeleteEntrepriseClicked(entrepriseNom)
                dialog.dismiss()
            }
            .show()
    }

    private fun onEntrepriseClicked(entreprise: Entreprise) {
        startActivity(Intent(activity, DetailsEntrepriseActivity::class.java).putExtra("ENTREPRISE_ID", entreprise.nom))
    }
    private fun onDeleteEntrepriseClicked(entrepriseNom: String) {
        // Supprime l'entreprise basée sur son nom
        entrepriseDataRepository.deleteItem { it.nom == entrepriseNom }
        // Met à jour les entreprises affichées après suppression
        entrepriseAdapter.updateEntreprises(entrepriseDataRepository.getItems())
    }


    private fun onEditEntrepriseClicked(entrepriseNom: String) {
        startActivity(Intent(activity, EditEntrepriseActivity::class.java).putExtra("ENTREPRISE_ID", entrepriseNom))
    }

    override fun onResume() {
        super.onResume()
        entrepriseAdapter.updateEntreprises(entrepriseDataRepository.getItems())
    }
}
