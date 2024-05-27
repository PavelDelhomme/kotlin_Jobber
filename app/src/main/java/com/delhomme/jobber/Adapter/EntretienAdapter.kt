package com.delhomme.jobber.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Locale

class EntretienAdapter(
    var entretiens: List<Entretien>,
    private val dataRepository: DataRepository,
    private val itemClickListener: (Entretien) -> Unit,
    private val deleteClickListener: (String) -> Unit,
    private val editClickListener: (String) -> Unit
) : RecyclerView.Adapter<EntretienAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateEntretien: TextView = view.findViewById(R.id.dateEntretien)
        val entrepriseEntretien: TextView = view.findViewById(R.id.entrepriseEntretien)
        val typeEntretien: TextView = view.findViewById(R.id.typeEntretien)

        fun bind(
            entretien: Entretien,
            dataRepository: DataRepository,
            clickListener: (Entretien) -> Unit,
            deleteListener: (String) -> Unit,
            editListener: (String) -> Unit
        ) {
            val entrepriseName = dataRepository.getEntrepriseByNom(entretien.entrepriseNom)?.nom ?: "Entreprise inconnue"
            dateEntretien.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(entretien.date_entretien)
            entrepriseEntretien.text = entrepriseName
            typeEntretien.text = entretien.type

            itemView.setOnClickListener { clickListener(entretien) }
            itemView.setOnLongClickListener {
                editListener(entretien.id)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entretien, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entretiens[position], dataRepository, itemClickListener, deleteClickListener, editClickListener)
    }

    override fun getItemCount() = entretiens.size

    fun updateEntretiens(newList: List<Entretien>) {
        entretiens = newList
        notifyDataSetChanged()
    }
}
