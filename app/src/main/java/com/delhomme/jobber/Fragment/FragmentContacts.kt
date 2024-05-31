package com.delhomme.jobber.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Activity.Contact.AddContactActivity
import com.delhomme.jobber.Activity.Contact.DetailsContactActivity
import com.delhomme.jobber.Activity.Contact.EditContactActivity
import com.delhomme.jobber.Adapter.ContactAdapter
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.R
import com.delhomme.jobber.Utils.SwipeCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FragmentContacts : Fragment() {
    private lateinit var adapter: ContactAdapter
    private lateinit var contactDataRepository: ContactDataRepository
    private lateinit var entrepriseDataRepository: EntrepriseDataRepository
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Contacts"

        contactDataRepository = ContactDataRepository(requireContext())
        entrepriseDataRepository = EntrepriseDataRepository(requireContext())
        initUI(view)

    }

    private fun initUI(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContacts)
        contactAdapter = ContactAdapter(
            contactDataRepository.getItems(),
            contactDataRepository,
            entrepriseDataRepository,
            this::onContactClicked,
            this::onDeleteContactClicked,
            this::onEditContactClicked
        )
        recyclerView.adapter = contactAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            startActivity(Intent(activity, AddContactActivity::class.java))
        }

        setupSwipeCallback(recyclerView)
    }


    private fun setupSwipeCallback(recyclerView: RecyclerView) {
        val swipeCallback = SwipeCallback(requireActivity(),
            { position -> showDeleteConfirmationDialog(contactAdapter.contacts[position].id, position) },
            { position -> onEditContactClicked(contactAdapter.contacts[position].id) }
        )
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }

    private fun showDeleteConfirmationDialog(contactId: String, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmation de suppression")
            .setMessage("Voulez-vous vraiment supprimer ce contact ?")
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Supprimer") { _, _ -> onDeleteContactClicked(contactId) }
            .show()
    }

    private fun onContactClicked(contact: Contact) {
        val intent = Intent(activity, DetailsContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
        }
        startActivity(intent)
    }

    private fun onDeleteContactClicked(contactId: String) {
        contactDataRepository.deleteContact(contactId)
        contactAdapter.updateContacts(contactDataRepository.getItems())
    }

    private fun onEditContactClicked(contactId: String) {
        startActivity(Intent(activity, EditContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contactId)
        })
    }
}
