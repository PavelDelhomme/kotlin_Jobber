package com.delhomme.jobber.CandidaturePacket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R

class CandidatureAdapter(
    private val candidatures: List<Candidature>,
    private val onItemClickListener: (Candidature) -> Unit
) : RecyclerView.Adapter<CandidatureAdapter.CandidatureViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidatureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_candidature, parent, false)
        return CandidatureViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: CandidatureViewHolder, position: Int) {
        val candidature = candidatures[position]
        holder.bind(candidature)
    }

    override fun getItemCount(): Int = candidatures.size

    class CandidatureViewHolder(
        itemView: View,
        private val onItemClickListener: (Candidature) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val txtTitreOffre = itemView.findViewById<TextView>(R.id.txtTitreOffre)
        private val txtEntreprise = itemView.findViewById<TextView>(R.id.txtEntreprise)
        private val txtDateCandidature = itemView.findViewById<TextView>(R.id.txtDateCandidature)

        fun bind(candidature: Candidature) {
            txtDateCandidature.text = candidature.date
            txtTitreOffre.text = candidature.titreOffre
            txtEntreprise.text = candidature.entrepriseNom

            itemView.setOnClickListener {
                onItemClickListener(candidature)
            }
        }
    }
}
