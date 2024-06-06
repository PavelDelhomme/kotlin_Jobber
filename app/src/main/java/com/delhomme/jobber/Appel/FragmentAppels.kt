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
import com.delhomme.jobber.Activity.Appel.DetailsAppelActivity
import com.delhomme.jobber.Activity.Appel.EditAppelActivity
import com.delhomme.jobber.Adapter.AppelAdapter
import com.delhomme.jobber.Api.Repository.AppelDataRepository
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Appel.AddAppelActivity
import com.delhomme.jobber.Candidature.SwipeCallback
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FragmentAppels : Fragment() {
    private lateinit var appelAdapter: AppelAdapter
    private lateinit var appelDataRepository: AppelDataRepository
    private lateinit var contactDataRepository: ContactDataRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_appels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Appels"

        appelDataRepository = AppelDataRepository(requireContext())
        contactDataRepository = ContactDataRepository(requireContext())
        initUI(view)
    }
    private fun initUI(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvAppels)
        val emptyView = view.findViewById<TextView>(R.id.empty_view_appels)

        appelAdapter = AppelAdapter(
            appelDataRepository.getItems(),
            appelDataRepository,
            contactDataRepository,
            this::onAppelClicked,
            this::onDeleteAppelClicked,
            this::onEditAppelClicked
        )
        recyclerView.adapter = appelAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        updateUI(recyclerView, emptyView)

        view.findViewById<Button>(R.id.btnAddAppel).setOnClickListener {
            startActivity(Intent(activity, AddAppelActivity::class.java))
        }

        setupSwipeCallback(recyclerView)
    }

    private fun setupSwipeCallback(recyclerView: RecyclerView) {
        val swipeCallback = SwipeCallback(requireActivity(),
            { position -> showDeleteConfirmationDialog(appelAdapter.appels[position].id, position) },
            { position -> onEditAppelClicked(appelAdapter.appels[position].id) }
        )
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }

    private fun updateUI(recyclerView: RecyclerView, emptyView: TextView) {
        if (appelAdapter.itemCount > 0) {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }
    private fun showDeleteConfirmationDialog(appelId: String, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmation de suppression")
            .setMessage("Voulez-vous vraiment supprimer cet Appel ?")
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Supprimer") { _, _ -> onDeleteAppelClicked(appelId) }
            .show()
    }

    private fun onAppelClicked(appel: Appel) {
        startActivity(Intent(activity, DetailsAppelActivity::class.java).putExtra("APPEL_ID", appel.id))
    }

    private fun onDeleteAppelClicked(appelId: String) {
        appelDataRepository.deleteAppel(appelId)
        appelAdapter.updateAppels(appelDataRepository.getItems())
        updateUI(view?.findViewById(R.id.rvAppels) as RecyclerView, view?.findViewById(R.id.empty_view_appels) as TextView)
    }

    private fun onEditAppelClicked(appelId: String) {
        startActivity(Intent(activity, EditAppelActivity::class.java).putExtra("APPEL_ID", appelId))
    }

    override fun onResume() {
        super.onResume()
        appelAdapter.updateAppels(appelDataRepository.getItems())
    }
}
