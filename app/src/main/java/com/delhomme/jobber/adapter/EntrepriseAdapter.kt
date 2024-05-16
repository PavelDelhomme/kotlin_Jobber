package com.delhomme.jobber.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Entreprise

class EntrepriseAdapter(
    private var entreprises: List<Entreprise>,
    private val ItemClickListener: (Entreprise) -> Unit,
    private val deleteClickListener: (String) -> Unit
    ) : RecyclerView.Adapter<EntrepriseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvEntrepriseName: TextView = view.findViewById(R.id.tvEntrepriseName)
        private val btnDelete: Button = view.findViewById(R.id.btnDeleteEntreprise)

        fun bind(entreprise: Entreprise, clickListener: (Entreprise) -> Unit, deleteListener: (String) -> Unit) {
            tvEntrepriseName.text = entreprise.nom
            itemView.setOnClickListener { clickListener(entreprise) }
            btnDelete.setOnClickListener { deleteListener(entreprise.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entreprise, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entreprises[position], ItemClickListener, deleteClickListener)
    }

    override fun getItemCount(): Int = entreprises.size

    fun updateEntreprises(newEntreprises: List<Entreprise>) {
        entreprises = newEntreprises
        notifyDataSetChanged()
    }
}
