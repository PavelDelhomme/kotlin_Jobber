package com.delhomme.jobber.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Candidature

class CandidatureAdapter(private val candidatures: List<Candidature>) :
    RecyclerView.Adapter<CandidatureAdapter.CandidatureViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidatureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_candidature, parent, false)
        return CandidatureViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandidatureViewHolder, position: Int) {
        val candidature = candidatures[position]
        holder.bind(candidature)
    }

    override fun getItemCount(): Int {
        return candidatures.size
    }

    class CandidatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTitreOffre = itemView.findViewById<TextView>(R.id.txtTitreOffre)
        private val txtEntreprise = itemView.findViewById<TextView>(R.id.txtEntreprise)

        fun bind(candidature: Candidature) {
            txtTitreOffre.text = candidature.titreOffre
            txtEntreprise.text = candidature.entrepriseNom
        }

    }
}
