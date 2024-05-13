package com.delhomme.jobber.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Candidature
import java.text.SimpleDateFormat
import java.util.Locale

class CandidatureAdapter(
    private var candidatures: List<Candidature>,
    private val itemClickListener: (Candidature) -> Unit,
    private val deleteClickListener: (String) -> Unit
    ) : RecyclerView.Adapter<CandidatureAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nomPoste: TextView = view.findViewById(R.id.nomPoste)
        private val entreprise: TextView = view.findViewById(R.id.entreprise)
        private val date: TextView = view.findViewById(R.id.date)
        private val etat: TextView = view.findViewById(R.id.etat)
        private val btnDelete: Button = view.findViewById(R.id.btnDeleteCandidature)

        fun bind(candidature: Candidature, clickListener: (Candidature) -> Unit, deleteListener: (String) -> Unit) {
            nomPoste.text = candidature.titre_offre
            entreprise.text = candidature.entreprise.nom
            date.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(candidature.date_candidature)
            etat.text = candidature.etat
            itemView.setOnClickListener { clickListener(candidature) }
            btnDelete.setOnClickListener { deleteListener(candidature.id) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_candidature, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(candidatures[position], itemClickListener, deleteClickListener)
    }

    override fun getItemCount(): Int = candidatures.size

    fun updateList(newList: List<Candidature>) {
        candidatures = newList
        notifyDataSetChanged()
    }
}
