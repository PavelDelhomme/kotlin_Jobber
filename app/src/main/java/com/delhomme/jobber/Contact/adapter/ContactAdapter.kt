package com.delhomme.jobber.Contact.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R


class ContactAdapter(private var contacts: List<Contact>,
                     private val dataRepository: DataRepository,
                     private val itemClickListener: (Contact) -> Unit,
                     private val deleteClickListener: (String) -> Unit,
                     private val editClickListener: (String) -> Unit
                     ) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullNameContact: TextView = view.findViewById(R.id.contactFullName)
        val entreprise: TextView = view.findViewById(R.id.entreprise)
        val telephone: TextView = view.findViewById(R.id.telephoneContact)
        val email: TextView = view.findViewById(R.id.emailContact)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteContact)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditContact)

        fun bind(contact: Contact, dataRepository: DataRepository, clickListener: (Contact) -> Unit, deleteListener: (String) -> Unit, editListener: (String) -> Unit) {
            val testEntreprise = dataRepository.getEntrepriseByNom(contact.entrepriseNom)
            Log.d("ContactAdapter", "Nom de l'entreprise grace a dataRepository.getEntrepriseByNom(contact.entrepriseNom)")
            Log.d("ContactAdapter", "${dataRepository.getEntrepriseByNom(contact.entrepriseNom)}")
            Log.d("ContactAdapter", "${testEntreprise?.nom}")
            val entrepriseName = dataRepository.getEntrepriseByNom(contact.entrepriseNom)?.nom ?: "Entreprise inconnue"
            fullNameContact.text = contact.getFullName()
            entreprise.text = entrepriseName
            telephone.text = contact.telephone
            email.text = contact.email
            itemView.setOnClickListener { clickListener(contact) }
            btnEdit.setOnClickListener { editListener(contact.id) }
            btnDelete.setOnClickListener { deleteListener(contact.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contacts[position], dataRepository, itemClickListener, deleteClickListener, editClickListener)
    }

    override fun getItemCount() = contacts.size

    fun updateContacts(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }
}
