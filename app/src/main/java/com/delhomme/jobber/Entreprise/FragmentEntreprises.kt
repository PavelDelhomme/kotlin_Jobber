package com.delhomme.jobber.Entreprise

import EntrepriseAdapter
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
import com.delhomme.jobber.Candidature.SwipeCallback
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.Entreprise.model.Entreprise
import com.delhomme.jobber.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FragmentEntreprises : Fragment() {
    private lateinit var adapter: EntrepriseAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_entreprises, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Entreprises"

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvEntreprises)
        adapter = EntrepriseAdapter(dataRepository.getEntreprises(), this::onEntrepriseClicked, this::onDeleteEntrepriseClicked, this::onEditEntrepriseClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
       val emptyView = view.findViewById<TextView>(R.id.empty_view_entreprises)

        updateUI(recyclerView, emptyView)

        val swipeCallback = SwipeCallback(requireContext(),
            { position ->
                val entreprise = adapter.entreprises[position]
                showDeleteConfirmationDialog(entreprise.nom, position)
            },
            { position ->
                val entreprise = adapter.entreprises[position]
                onEditEntrepriseClicked(entreprise.nom)
            }
        )

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
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
    private fun showDeleteConfirmationDialog(entrepriseNom: String, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmation de suppression")
            .setMessage("Voulez-vous vraiment supprimer cette entreprise ?")
            .setNegativeButton("Annuler") { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setPositiveButton("Supprimer") { dialog, _ ->
                onDeleteEntrepriseClicked(entrepriseNom)
                dialog.dismiss()
            }
            .show()
    }

    private fun onEntrepriseClicked(entreprise: Entreprise) {
        val intent = Intent(activity, DetailsEntrepriseActivity::class.java).apply {
            putExtra("ENTREPRISE_ID", entreprise.nom)
        }
        startActivity(intent)
    }

    private fun onDeleteEntrepriseClicked(entrepriseId: String) {
        dataRepository.deleteEntreprise(entrepriseId)
        adapter.updateEntreprises(dataRepository.getEntreprises())
    }

    private fun onEditEntrepriseClicked(entrepriseNom: String) {
        val intent = Intent(activity, EditEntrepriseActivity::class.java).apply {
            putExtra("ENTREPRISE_ID", entrepriseNom)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateEntreprises(dataRepository.getEntreprises())
    }
}
