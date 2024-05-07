// ContactAdapter.kt
package com.delhomme.jobber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.models.Contact

class ContactAdapter(private val contacts: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNom = itemView.findViewById<TextView>(R.id.txtNom)
        private val txtPrenom = itemView.findViewById<TextView>(R.id.txtPrenom)

        fun bind(contact: Contact) {
            txtNom.text = contact.nom
            txtPrenom.text = contact.prenom
        }
    }
}
