package com.delhomme.jobber.Contact.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import com.delhomme.jobber.Contact.model.Contact


class ContactAdapter(private var contacts: List<Contact>,
                     private val dataRepository: DataRepository,
                     private val itemClickListener: (Contact) -> Unit,
                     private val deleteClickListener: (String) -> Unit
                     ) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullNameContact: TextView = view.findViewById(R.id.contactFullName)
        val entreprise: TextView = view.findViewById(R.id.entreprise)
        val telephone: TextView = view.findViewById(R.id.telephoneContact)
        val email: TextView = view.findViewById(R.id.emailContact)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteContact)
        fun bind(contact: Contact, dataRepository: DataRepository, clickListener: (Contact) -> Unit, deleteListener: (String) -> Unit) {
            val entrepriseName = dataRepository.getEntrepriseById(contact.entrepriseId)?.nom ?: "Entreprise inconnue"
            fullNameContact.text = contact.getFullName()
            entreprise.text = entrepriseName
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
        holder.bind(contacts[position], dataRepository, itemClickListener, deleteClickListener)
    }

    override fun getItemCount() = contacts.size

    fun updateContacts(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }
}
