package com.delhomme.jobber.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Contact


class ContactAdapter(
    private var contacts: List<Contact>,
    private val listener: OnContactClickListener
) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    interface OnContactClickListener {
        fun onContactClick(contact: Contact?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact, listener)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun updateList(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contactName: TextView

        init {
            contactName = itemView.findViewById<TextView>(R.id.contactName)
        }

        fun bind(contact: Contact, listener: OnContactClickListener) {
            contactName.setText(contact.getFullName())
            itemView.setOnClickListener { v: View? ->
                listener.onContactClick(
                    contact
                )
            }
        }
    }
}