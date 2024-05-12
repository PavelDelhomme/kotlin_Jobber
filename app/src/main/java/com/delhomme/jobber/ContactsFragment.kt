package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.ContactAdapter
import com.delhomme.jobber.adapter.ContactAdapter.OnContactClickListener
import com.delhomme.jobber.models.Contact

class ContactsFragment : Fragment(), OnContactClickListener {
    private var adapter: ContactAdapter? = null
    private var dataRepository: DataRepository? = null
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
        dataRepository = DataRepository(requireContext())
        val contacts: List<Contact> = dataRepository!!.loadContacts()
        adapter = ContactAdapter(contacts, this)
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

    override fun onContactClick(contact: Contact?) {
        // Gérer le clic sur un contact
    }

    override fun onResume() {
        super.onResume()
        dataRepository?.let { adapter!!.updateList(it.loadContacts()) }
    }
}
