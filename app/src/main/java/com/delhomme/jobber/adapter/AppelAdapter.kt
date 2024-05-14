package com.delhomme.jobber.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Appel
import java.text.SimpleDateFormat
import java.util.Locale

class AppelAdapter(
    private var appels: List<Appel>,
    private val itemClickListener: (Appel) -> Unit,
    private val deleteClickListener: (String) -> Unit
    ) : RecyclerView.Adapter<AppelAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewDate: TextView = view.findViewById(R.id.tvDateAppel)
        private val textViewObjet: TextView = view.findViewById(R.id.tvObjetAppel)
        private val textViewNotes: TextView = view.findViewById(R.id.tvNotesAppels)
        private val btnDelete: Button = view.findViewById(R.id.btnDeleteAppel)

        fun bind(appel: Appel, clickListener: (Appel) -> Unit, deleteListener: (String) -> Unit) {
            textViewDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(appel.date_appel)
            textViewObjet.text = appel.objet
            textViewNotes.text = appel.notes

            itemView.setOnClickListener { clickListener(appel) }
            btnDelete.setOnClickListener { deleteListener(appel.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appels[position], itemClickListener, deleteClickListener)
    }

    override fun getItemCount() = appels.size

    fun updateAppels(newList: List<Appel>) {
        appels = newList
        notifyDataSetChanged()
    }
}
