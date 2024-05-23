package com.delhomme.jobber.Candidature.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.CandidatureState
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class CandidatureAdapter(
    var candidatures: List<Candidature>,
    private var dataRepository: DataRepository,
    private val itemClickListener: (Candidature) -> Unit,
    private val deleteClickListener: (String) -> Unit,
    private val editClickListener: (String) -> Unit
    ) : RecyclerView.Adapter<CandidatureAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nomPoste: TextView = view.findViewById(R.id.nomPoste)
        private val entreprise: TextView = view.findViewById(R.id.entreprise)
        private val date: TextView = view.findViewById(R.id.date_candidature)
        private val etat: TextView = view.findViewById(R.id.etat)
        private val typePoste: TextView = view.findViewById(R.id.typePoste)
        private val plateforme: TextView = view.findViewById(R.id.plateforme)
        private val notes: TextView = view.findViewById(R.id.tvNotesCandidature)

        fun bind(candidature: Candidature, dataRepository: DataRepository, clickListener: (Candidature) -> Unit, deleteListener: (String) -> Unit, editListener: (String) -> Unit) {
            val cardView: CardView = itemView.findViewById(R.id.cardView2)
            cardView.setBackgroundColor(itemView.context.resources.getColor(getStateColor(candidature.state), null))
            val entrepriseNom = dataRepository.getEntrepriseByNom(candidature.entrepriseNom)?.nom ?: "Unknown Entreprise"

            nomPoste.text = candidature.titre_offre
            entreprise.text = entrepriseNom
            date.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(candidature.date_candidature)
            etat.text = candidature.state.toString()
            etat.text = when (candidature.state) {
                CandidatureState.CANDIDATEE_ET_EN_ATTENTE -> "🕒 Candidature en attente"
                CandidatureState.EN_ATTENTE_APRES_ENTRETIEN -> "🕒 En attente après entretien"
                CandidatureState.EN_ATTENTE_D_UN_ENTRETIEN -> "🕒 En attente d'un entretien"
                CandidatureState.FAIRE_UN_RETOUR_POST_ENTRETIEN -> "🔄 Faire un retour post entretien"
                CandidatureState.A_RELANCEE_APRES_ENTRETIEN -> "🔄 Relancée après entretien"
                CandidatureState.A_RELANCEE -> "🔄 À relancer"
                CandidatureState.RELANCEE_ET_EN_ATTENTE -> "🕒 Relancée et en attente"
                CandidatureState.AUCUNE_REPONSE -> "🚫 Aucune réponse"
                CandidatureState.NON_RETENU -> "❌ Non retenue"
                CandidatureState.ERREUR -> "⚠️ Erreur"
                CandidatureState.NON_RETENU_APRES_ENTRETIEN -> "❌️ Non retenue après entretien"
                CandidatureState.NON_RETENU_SANS_ENTRETIEN -> "❌ Non retenue"
            }

            typePoste.text = candidature.type_poste
            plateforme.text = candidature.plateforme
            if (candidature.notes == "") {
                notes.text = "Pas de notes sur la candidature"
            } else {
                notes.text = candidature.notes
            }

            itemView.setOnClickListener { clickListener(candidature) }
        }

        private fun getStateColor(state: CandidatureState): Int {
            return when(state) {
                CandidatureState.CANDIDATEE_ET_EN_ATTENTE -> R.color.colorState1
                CandidatureState.EN_ATTENTE_APRES_ENTRETIEN -> R.color.colorState2
                CandidatureState.EN_ATTENTE_D_UN_ENTRETIEN -> R.color.colorState3
                CandidatureState.FAIRE_UN_RETOUR_POST_ENTRETIEN -> R.color.colorState4
                CandidatureState.A_RELANCEE_APRES_ENTRETIEN -> R.color.colorState5
                CandidatureState.A_RELANCEE -> R.color.colorState6
                CandidatureState.RELANCEE_ET_EN_ATTENTE -> R.color.colorState7
                CandidatureState.AUCUNE_REPONSE -> R.color.colorState8
                CandidatureState.NON_RETENU -> R.color.colorState9
                CandidatureState.ERREUR -> R.color.colorState10
                CandidatureState.NON_RETENU_APRES_ENTRETIEN -> R.color.colorState11
                CandidatureState.NON_RETENU_SANS_ENTRETIEN -> R.color.colorState12
            }
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
        this.candidatures = newList
        notifyDataSetChanged()
        Log.d("CandidatureAdapter", "Candidatures updated: ${newList.size}")
    }

    fun removeItem(position: Int) {
        if (position < itemCount) {
            candidatures = candidatures.toMutableList().also { it.removeAt(position) }
            notifyItemRemoved(position)
        }
    }

}
