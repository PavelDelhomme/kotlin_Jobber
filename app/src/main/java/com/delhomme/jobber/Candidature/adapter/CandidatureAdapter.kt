package com.delhomme.jobber.Candidature.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class CandidatureAdapter(
    private var candidatures: List<Candidature>,
    private var dataRepository: DataRepository,
    private val itemClickListener: (Candidature) -> Unit,
    private val deleteClickListener: (String) -> Unit,
    private val editClickListener: (String) -> Unit
    ) : RecyclerView.Adapter<CandidatureAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nomPoste: TextView = view.findViewById(R.id.nomPoste)
        private val entreprise: TextView = view.findViewById(R.id.entreprise)
        private val date: TextView = view.findViewById(R.id.date)
        private val etat: TextView = view.findViewById(R.id.etat)
        private val spinnertypePoste: Spinner = view.findViewById(R.id.spinner_type_poste)
        private val spinnerplateforme: Spinner = view.findViewById(R.id.spinner_plateforme)
        private val notes: TextView = view.findViewById(R.id.tvNotesCandidature)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteCandidature)
        private val btnEdit: ImageButton = view.findViewById(R.id.btnEditCandidature)

        fun bind(candidature: Candidature, dataRepository: DataRepository, clickListener: (Candidature) -> Unit, deleteListener: (String) -> Unit, editListener: (String) -> Unit) {
            val entrepriseNom = dataRepository.getEntrepriseById(candidature.entrepriseId)?.nom ?: "Unknown Entreprise"

            val context = itemView.context
            val typePosteAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, dataRepository.getTypePosteOptions())
            spinnertypePoste.adapter = typePosteAdapter
            spinnertypePoste.setSelection(typePosteAdapter.getPosition(candidature.type_poste))

            val plateformeAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, dataRepository.getPlateformeOptions())
            spinnerplateforme.adapter = plateformeAdapter
            spinnertypePoste.setSelection(plateformeAdapter.getPosition(candidature.plateforme))

            nomPoste.text = candidature.titre_offre
            entreprise.text = entrepriseNom
            date.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(candidature.date_candidature)
            etat.text = candidature.etat
            //notes.text = candidature.notes
            itemView.setOnClickListener { clickListener(candidature) }
            btnEdit.setOnClickListener { editListener(candidature.id) }
            btnDelete.setOnClickListener { deleteListener(candidature.id) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_candidature, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(candidatures[position],dataRepository, itemClickListener, deleteClickListener, editClickListener)
    }

    override fun getItemCount(): Int = candidatures.size

    fun updateCandidatures(newList: List<Candidature>) {
        candidatures = newList
        notifyDataSetChanged()
    }
}
