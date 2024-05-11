package com.delhomme.jobber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.models.Evenement

class EvenementAdapter(private val evenements: List<Evenement>) :
    RecyclerView.Adapter<EvenementAdapter.EvenementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvenementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evenement, parent, false)
        return EvenementViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvenementViewHolder, position: Int) {
        val evenement = evenements[position]
        holder.bind(evenement)
    }

    override fun getItemCount(): Int {
        return evenements.size
    }

    class EvenementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtType = itemView.findViewById<TextView>(R.id.txtType)
        private val txtDate = itemView.findViewById<TextView>(R.id.txtDate)

        fun bind(evenement: Evenement) {
            txtType.text = evenement.type
            txtDate.text = evenement.date
        }
    }

}
