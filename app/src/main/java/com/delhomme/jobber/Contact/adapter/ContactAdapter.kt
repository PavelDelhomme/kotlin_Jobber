package com.delhomme.jobber.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Api.Repository.ContactDataRepository
import com.delhomme.jobber.Api.Repository.EntrepriseDataRepository
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.R


class ContactAdapter(
    var contacts: List<Contact>,
    private val contactDataRepository: ContactDataRepository,
    private val entrepriseDataRepository: EntrepriseDataRepository,
    private val itemClickListener: (Contact) -> Unit,
    private val deleteClickListener: (String) -> Unit,
    private val editClickListener: (String) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullNameContact: TextView = view.findViewById(R.id.contactFullName)
        val entreprise: TextView = view.findViewById(R.id.entrepriseContact)
        val telephone: TextView = view.findViewById(R.id.telephoneContact)
        val email: TextView = view.findViewById(R.id.emailContact)

        fun bind(contact: Contact, entrepriseDataRepository: EntrepriseDataRepository, clickListener: (Contact) -> Unit, deleteListener: (String) -> Unit, editListener: (String) -> Unit) {
            val entrepriseName = entrepriseDataRepository.findByCondition { it.nom == contact.entreprise }.firstOrNull()?.nom ?: "Entreprise inconnue"
            fullNameContact.text = contact.getFullName()
            entreprise.text = entrepriseName
            telephone.text = contact.telephone
            email.text = contact.email

            itemView.setOnClickListener { clickListener(contact) }
            itemView.setOnLongClickListener {
                editListener(contact.id)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contacts[position], entrepriseDataRepository, itemClickListener, deleteClickListener, editClickListener)
    }

    override fun getItemCount() = contacts.size

    fun updateContacts(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }
}
