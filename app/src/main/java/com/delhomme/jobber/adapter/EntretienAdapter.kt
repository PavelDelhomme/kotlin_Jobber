package com.delhomme.jobber.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Entretien
import java.text.SimpleDateFormat
import java.util.Locale

class EntretienAdapter(
    private var entretiens: List<Entretien>,
    private var dataRepository: DataRepository,
    private val itemClickListener: (Entretien) -> Unit,
    private val deleteClickListener: (String) -> Unit
    ) : RecyclerView.Adapter<EntretienAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dateEntretien: TextView = view.findViewById(R.id.dateEntretien)
        private val typeEntretien: TextView = view.findViewById(R.id.typeEntretien)
        //private val modeEntretien: TextView = view.findViewById(R.id.modeEntretien)
        //private val notesEntretien: TextView = view.findViewById(R.id.notesEntretien)
        private val entreprise: TextView = view.findViewById(R.id.entrepriseEntretien)
        private val candidatureTitre: TextView = view.findViewById(R.id.candidatureTitreEntretien)
        private val contactNom: TextView = view.findViewById(R.id.nomContactEntretien)
        private val deleteButton: Button = view.findViewById(R.id.btnDeleteEntretien)

        fun bind(entretien: Entretien, dataRepository: DataRepository ,clickListener: (Entretien) -> Unit, deleteListener: (String) -> Unit) {
            val entrepriseName = dataRepository.getEntrepriseById(entretien.entreprise_id)?.nom ?: "Entreprise inconnue"
            val candidature = dataRepository.getCandidatureById(entretien.entreprise_id)?.titre_offre ?: "Offre inconnue"
            val contact = dataRepository.getContactById(entretien.contact_id)?.getFullName() ?: "Aucun contact pour cet entretien"
            dateEntretien.text = SimpleDateFormat("dd/MM/yyyyy", Locale.getDefault()).format(entretien.date_entretien)
            typeEntretien.text = entretien.type
            //modeEntretien.text = entretien.mode
            //notesEntretien.text = entretien.notes_pre_entretien ?: "Aucune notes de pré-entretien"
            entreprise.text = entrepriseName
            candidatureTitre.text = candidature
            contactNom.text = contact

            itemView.setOnClickListener { clickListener(entretien) }
            deleteButton.setOnClickListener { deleteListener(entretien.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entretien, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entretiens[position],dataRepository, itemClickListener, deleteClickListener)
    }

    override fun getItemCount() = entretiens.size

    fun updateEntretiens(newEntretiens: List<Entretien>) {
        entretiens = newEntretiens
        notifyDataSetChanged()
    }
}