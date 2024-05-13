package com.delhomme.jobber.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.models.Entreprise

class EntrepriseAdapter(private var entreprises: List<Entreprise>, private val onItemClick: (Entreprise) -> Unit) : RecyclerView.Adapter<EntrepriseAdapter.ViewHolder>() {

    class ViewHolder(view: View, private val onItemClick: (Entreprise) -> Unit) : RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(android.R.id.text1)

        fun bind(entreprise: Entreprise) {
            nameView.text = entreprise.nom
            itemView.setOnClickListener { onItemClick(entreprise) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entreprises[position])
    }

    override fun getItemCount(): Int = entreprises.size

    fun updateEntreprises(newEntreprises: List<Entreprise>) {
        entreprises = newEntreprises
        notifyDataSetChanged()
    }
}
