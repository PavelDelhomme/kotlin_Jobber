package com.delhomme.jobber.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.R
import com.delhomme.jobber.Model.Relance
import java.text.SimpleDateFormat
import java.util.Locale

class RelanceAdapter(
    var relances: List<Relance>,
    private val dataRepository: DataRepository,
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

        fun bind(relance: Relance, dataRepository: DataRepository, clickListener: (Relance) -> Unit, deleteListener: (String) -> Unit, editListener: (String) -> Unit) {
            val entrepriseName = dataRepository.getEntrepriseByNom(relance.entrepriseNom)?.nom ?: "Entreprise inconnue"
            val candidature = dataRepository.getCandidatureById(relance.candidatureId)?.titre_offre ?: "Offre inconnue"
            dateRelance.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(relance.date_relance)
            entreprise.text = entrepriseName
            candidatureTitre.text = candidature
            plateformeUtilise.text = relance.plateformeUtilisee
            notesRelance.text = relance.notes ?: "Aucune note de relance"

            itemView.setOnClickListener { clickListener(relance) }
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
        holder.bind(relances[position], dataRepository, itemClickListener, deleteClickListener, editClickListener)
    }

    override fun getItemCount() = relances.size

    fun updateRelances(newRelances: List<Relance>) {
        relances = newRelances
        notifyDataSetChanged()
    }
}
