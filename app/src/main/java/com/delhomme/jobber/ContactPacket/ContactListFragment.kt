package com.delhomme.jobber.ContactPacket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Contact

class ContactListFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        val contactRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerContacts)
        contactRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val contacts = listOf<Contact>()

        val adapter = ContactAdapter(contacts)
        contactRecyclerView.adapter = adapter

        return view
    }
}