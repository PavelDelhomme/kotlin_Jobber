package com.delhomme.jobber.Appel

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Appel.adapter.AppelAdapter
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.Candidature.SwipeCallback
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
        val emptyView = view.findViewById<TextView>(R.id.empty_view_appels)


        adapter = AppelAdapter(
            dataRepository.getAppels(),
            dataRepository,
            this::onAppelClicked,
            this::onDeleteAppelClicked,
            this::onEditAppelClicked
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        updateUI(recyclerView, emptyView)

        view.findViewById<Button>(R.id.btnAddAppel).setOnClickListener {
            startActivity(Intent(activity, AddAppelActivity::class.java))
        }

        val swipeCallback = SwipeCallback(requireContext(),
            { position ->
                val appel = adapter.appels[position]
                showDeleteConfirmationDialog(appel.id, position)
            },
            { position ->
                val appel = adapter.appels[position]
                onEditAppelClicked(appel.id)
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
    private fun showDeleteConfirmationDialog(appelId: String, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmation de suppression")
            .setMessage("Voulez-vous vraiment supprimer cet appel ?")
            .setNegativeButton("Annuler") { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setPositiveButton("Supprimer") { dialog, _ ->
                onDeleteAppelClicked(appelId)
                dialog.dismiss()
            }
            .show()
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
        updateUI(view?.findViewById(R.id.rvAppels) as RecyclerView, view?.findViewById(R.id.empty_view_appels) as TextView)
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
