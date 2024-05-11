// ContactAdapter.kt
package com.delhomme.jobber.ContactPacket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R

class ContactAdapter(
    private val contacts: List<Contact>,
    private val onItemClickListener: (Contact) -> Unit
    ) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ContactViewHolder(
        itemView: View,
        private val onItemClickListener: (Contact) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val txtNom = itemView.findViewById<TextView>(R.id.txtNom)
        private val txtPrenom = itemView.findViewById<TextView>(R.id.txtPrenom)
        private val txtEntreprise = itemView.findViewById<TextView>(R.id.txtEntreprise)

        fun bind(contact: Contact) {
            txtNom.text = contact.nom
            txtPrenom.text = contact.prenom
            txtEntreprise.text = contact.entrepriseInstance.nom

            itemView.setOnClickListener {
                onItemClickListener(contact)
            }
        }
    }
}

