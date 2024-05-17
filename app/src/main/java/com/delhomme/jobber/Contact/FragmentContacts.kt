package com.delhomme.jobber.Contact

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Contact.adapter.ContactAdapter
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R

class FragmentContacts : Fragment() {
    private lateinit var adapter: ContactAdapter
    private val dataRepository by lazy { DataRepository(requireContext()) }
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContacts)
        adapter = ContactAdapter(dataRepository.getContacts(), dataRepository, this::onContactClicked, this::onDeleteContactClicked, this::onEditContactClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val addButton = view.findViewById<Button>(R.id.btnAddContact)
        addButton.setOnClickListener {
            startActivity(Intent(activity, AddContactActivity::class.java))
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                adapter.updateContacts(dataRepository.getContacts())
            }
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("CONTACTS_UPDATED"))
    }

    private fun onContactClicked(contact: Contact) {
        val intent = Intent(activity, DetailsContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contact.id)
        }
        startActivity(intent)
    }

    private fun onDeleteContactClicked(contactId: String) {
        dataRepository.deleteContact(contactId)
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(Intent("CONTACTS_UPDATED"))
    }

    private fun onEditContactClicked(contactId: String) {
        val intent = Intent(activity, EditContactActivity::class.java).apply {
            putExtra("CONTACT_ID", contactId)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateContacts(dataRepository.getContacts())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(broadcastReceiver)
    }
}
