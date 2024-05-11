package com.delhomme.jobber.ContactPacket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.google.gson.Gson

class ContactListFragment : Fragment() {
    private val contacts = mutableListOf<Contact>()
    private lateinit var contactAdapter: ContactAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        val contactRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerContacts)
        contactRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        contactAdapter = ContactAdapter(contacts) {contact ->
            showContactDetails(contact)
        }
        contactRecyclerView.adapter = contactAdapter

        loadContacts()

        return view
    }

    private fun showContactDetails(contact: Contact) {
        val intent = Intent(requireContext(), ContactDetailsActivity::class.java)
        intent.putExtra("contact_id", contact.id)
        startActivity(intent)
    }

    public fun loadContacts() {
        Log.e("ContactListFragment", "Chargement des contacts enregistrer")
        val sharedPreferences = requireContext().getSharedPreferences("contacts_prefs", 0)
        val gson = Gson()
        Log.e("ContactListFragment", "Suppression de la liste des contacts du coup")
        contacts.clear()

        for ((key, value) in sharedPreferences.all) {
            if (key.startsWith("contact_")) {
                val contactJson = value as String
                val contact = gson.fromJson(contactJson, Contact::class.java)
                Log.e("ajout du contact à la liste", "contact : $contact")
                contacts.add(contact)
            }
        }
        Log.e("ContactListFragment", "les contact ont été ajouté normalement depuis le cache")
        Log.e("ContactListFragment", "Liste de contacts : $contacts")
        contactAdapter.notifyDataSetChanged()
    }
}
