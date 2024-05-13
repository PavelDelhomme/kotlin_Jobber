package com.delhomme.jobber.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Contact


class ContactAdapter(private var contacts: List<Contact>,
                     private val itemClickListener: (Contact) -> Unit,
                     private val deleteClickListener: (String) -> Unit
                     ) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullNameContact: TextView = view.findViewById(R.id.contactFullName)
        val entreprise: TextView = view.findViewById(R.id.entreprise)
        val telephone: TextView = view.findViewById(R.id.telephoneContact)
        val email: TextView = view.findViewById(R.id.emailContact)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteContact)
        fun bind(contact: Contact, clickListener: (Contact) -> Unit, deleteListener: (String) -> Unit) {
            fullNameContact.text = contact.getFullName()
            entreprise.text = contact.entreprise?.nom ?: "Entreprise inconnue"
            telephone.text = contact.telephone
            email.text = contact.email
            itemView.setOnClickListener { clickListener(contact) }
            btnDelete.setOnClickListener { deleteListener(contact.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contacts[position], itemClickListener, deleteClickListener)
    }

    override fun getItemCount() = contacts.size

    fun updateContacts(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }
}
