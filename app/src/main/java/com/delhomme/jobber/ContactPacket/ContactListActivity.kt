package com.delhomme.jobber.ContactPacket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R
import com.google.gson.Gson

class ContactListActivity : AppCompatActivity() {
    private val contacts = mutableListOf<Contact>()
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val contactRecyclerView = findViewById<RecyclerView>(R.id.recyclerContacts)
        contactRecyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ContactAdapter(contacts)
        contactRecyclerView.adapter = adapter

        loadContacts()
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }

    private fun loadContacts() {
        Log.e("ContactListActivity loadContacts", "Loading contacts into ContactListActivity and no Fragment")
        val sharedPreferences = getSharedPreferences("contacts_prefs", MODE_PRIVATE)
        val gson = Gson()

        contacts.clear()

        for ((key, value) in sharedPreferences.all) {
            if (key.startsWith("contact_")) {
                val contactJson = value as String
                val contact = gson.fromJson(contactJson, Contact::class.java)
                contacts.add(contact)
            }
        }
        Log.e("ContactListActivity", "liste des contacts : $contacts")

        contactAdapter.notifyDataSetChanged()
    }
}