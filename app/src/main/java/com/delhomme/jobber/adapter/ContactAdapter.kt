package com.delhomme.jobber.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.ContactDetailActivity
import com.delhomme.jobber.models.Contact


class ContactAdapter(private var contacts: List<Contact>, private val context: Context) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = contacts[position].getFullName()
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ContactDetailActivity::class.java)
            intent.putExtra("CONTACT_ID", contacts[position].id)
            context.startActivity(intent)
        }
    }

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    override fun getItemCount() = contacts.size
}
