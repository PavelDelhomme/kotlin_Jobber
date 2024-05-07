// ContactListActivity.kt
package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.models.Contact

class ContactListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val contactRecyclerView = findViewById<RecyclerView>(R.id.recyclerContacts)
        contactRecyclerView.layoutManager = LinearLayoutManager(this)

        // Remplacez par une vraie liste récupérée
        val contacts = listOf<Contact>()

        val adapter = ContactAdapter(contacts)
        contactRecyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }
}
