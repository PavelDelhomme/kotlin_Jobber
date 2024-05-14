package com.delhomme.jobber.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Entretien
import java.text.SimpleDateFormat
import java.util.Locale

class EntretienAdapter(
    private var entretiens: List<Entretien>,
    private val itemClickListener: (Entretien) -> Unit,
    private val deleteClickListener: (String) -> Unit
    ) : RecyclerView.Adapter<EntretienAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dateEntretien: TextView = view.findViewById(R.id.dateEntretien)
        private val entreprise: TextView = view.findViewById(R.id.entreprise)
        private val typeEntretien: TextView = view.findViewById(R.id.typeEntretien)
        private val styleEntretien: TextView = view.findViewById(R.id.styleEntretien)
        private val notesEntretien: TextView = view.findViewById(R.id.notesEntretien)
        private val deleteButton: Button = view.findViewById(R.id.btnDeleteEntretien)

        fun bind(entretien: Entretien, clickListener: (Entretien) -> Unit, deleteListener: (String) -> Unit) {
                dateEntretien.text = SimpleDateFormat("dd/MM/yyyyy", Locale.getDefault()).format(entretien.date_entretien)
                entreprise.text = entretien.entrepriseNom
                typeEntretien.text = entretien.type_entretien
                styleEntretien.text = entretien.style_entretien
                notesEntretien.text = entretien.notes_pre_entretien ?: "Aucune notes de pré-entretien"

                itemView.setOnClickListener { clickListener(entretien) }
                deleteButton.setOnClickListener { deleteListener(entretien.id) }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entretien, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entretiens[position], itemClickListener, deleteClickListener)
    }

    override fun getItemCount() = entretiens.size

    fun updateEntretiens(newEntretiens: List<Entretien>) {
        entretiens = newEntretiens
        notifyDataSetChanged()
    }
}