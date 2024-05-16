package com.delhomme.jobber.Contact

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import com.delhomme.jobber.Contact.adapter.ContactAdapter
import com.delhomme.jobber.Contact.model.Contact

class FragmentContacts : Fragment() {
    private lateinit var adapter: ContactAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContacts)
        adapter = ContactAdapter(dataRepository.loadContacts(), dataRepository, this::onContactClicked, this::onDeleteContactClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val addButton = view.findViewById<Button>(R.id.btnAddContact)
        addButton.setOnClickListener { v: View? ->
            val intent = Intent(
                activity,
                AddContactActivity::class.java
            )
            startActivity(intent)
        }
    }

    private fun onContactClicked(contact: Contact) {
        val intent = Intent(activity, DetailsContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
        }
        startActivity(intent)
    }
    private fun onDeleteContactClicked(contactId: String) {
        dataRepository.deleteContact(contactId)
        adapter.updateContacts(dataRepository.loadContacts())
    }

    override fun onResume() {
        super.onResume()
        adapter.updateContacts(dataRepository.loadContacts())
    }
}
