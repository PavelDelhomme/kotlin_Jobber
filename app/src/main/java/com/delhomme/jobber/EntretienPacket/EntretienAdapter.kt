package com.delhomme.jobber.EntretienPacket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Entretien


class EntretienAdapter(private val entretiens: List<Entretien>) :
    RecyclerView.Adapter<EntretienAdapter.EntretienViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntretienViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entretien, parent, false)
        return EntretienViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntretienViewHolder, position: Int) {
        val entretien = entretiens[position]
        holder.bind(entretien)
    }

    override fun getItemCount(): Int {
        return entretiens.size
    }

    class EntretienViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtDateEntretien = itemView.findViewById<TextView>(R.id.txtDateEntretien)
        private val txtNotesPreparation = itemView.findViewById<TextView>(R.id.txtNotesPreparation)

        fun bind(entretien: Entretien) {
            txtDateEntretien.text = entretien.dateEntretien
            txtNotesPreparation.text = entretien.notesPreparation
        }
    }
}