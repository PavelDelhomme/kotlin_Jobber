package com.delhomme.jobber.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Candidature
import java.text.SimpleDateFormat
import java.util.Locale

class CandidatureAdapter(private var candidatures: List<Candidature>) : RecyclerView.Adapter<CandidatureAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomPoste: TextView = view.findViewById(R.id.nomPoste)
        val entreprise: TextView = view.findViewById(R.id.entreprise)
        val date: TextView = view.findViewById(R.id.date)
        val etat: TextView = view.findViewById(R.id.etat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_candidature, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val candidature = candidatures[position]
        holder.nomPoste.text = candidature.titre_offre
        holder.entreprise.text = candidature.entreprise.nom
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.date.text = dateFormat.format(candidature.date_candidature)
        holder.etat.text = candidature.etat
    }

    override fun getItemCount() = candidatures.size

    fun updateList(newList: List<Candidature>) {
        candidatures = newList
        notifyDataSetChanged()
    }
}
