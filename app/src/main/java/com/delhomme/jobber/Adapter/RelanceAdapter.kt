package com.delhomme.jobber.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.RelanceDataRepository
import com.delhomme.jobber.Model.Relance
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class RelanceAdapter(
    var relances: List<Relance>,
    private val relanceDataRepository: RelanceDataRepository,
    private val candidatureDataRepository: CandidatureDataRepository,
    private val itemClickListener: (Relance) -> Unit,
    private val deleteClickListener: (String) -> Unit,
    private val editClickListener: (String) -> Unit
) : RecyclerView.Adapter<RelanceAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateRelance: TextView = view.findViewById(R.id.dateRelance)
        val entreprise: TextView = view.findViewById(R.id.entrepriseRelance)
        val candidatureTitre: TextView = view.findViewById(R.id.candidatureTitre)
        val plateformeUtilise: TextView = view.findViewById(R.id.plateformeRelance)
        val notesRelance: TextView = view.findViewById(R.id.notesRelance)

        fun bind(
            relance: Relance,
            dataRepository: RelanceDataRepository,
            candidatureDataRepository: CandidatureDataRepository,
            itemClickListener: (Relance) -> Unit,
            deleteListener: (String) -> Unit,
            editListener: (String) -> Unit
        ) {
            val entrepriseName = dataRepository.findByCondition { it.entreprise == relance.entreprise }.firstOrNull()?.entreprise ?: "Entreprise inconnue"
            val candidature = candidatureDataRepository.findByCondition { it.id == relance.candidature }
                .firstOrNull()?.titre_offre ?: "Offre inconnue"
            dateRelance.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(relance.date_relance)
            entreprise.text = entrepriseName
            candidatureTitre.text = candidature
            plateformeUtilise.text = relance.plateforme_utilisee
            notesRelance.text = relance.notes ?: "Aucune note de relance"

            itemView.setOnClickListener { itemClickListener(relance) }
            itemView.setOnLongClickListener {
                editListener(relance.id)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_relance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(relances[position], relanceDataRepository, candidatureDataRepository, itemClickListener, deleteClickListener, editClickListener)
    }

    override fun getItemCount() = relances.size

    fun updateRelances(newRelances: List<Relance>) {
        relances = newRelances
        notifyDataSetChanged()
    }
}
