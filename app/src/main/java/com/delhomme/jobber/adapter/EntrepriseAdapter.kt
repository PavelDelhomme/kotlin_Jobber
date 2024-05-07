package com.delhomme.jobber.adapter



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import com.delhomme.jobber.models.Entreprise

class EntrepriseAdapter(private val entreprises: List<Entreprise>) :
    RecyclerView.Adapter<EntrepriseAdapter.EntrepriseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntrepriseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entreprise, parent, false)
        return EntrepriseViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntrepriseViewHolder, position: Int) {
        val entreprise = entreprises[position]
        holder.bind(entreprise)
    }

    override fun getItemCount(): Int {
        return entreprises.size
    }

    class EntrepriseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNomEntreprise = itemView.findViewById<TextView>(R.id.txtNomEntreprise)
        private val txtSecteurActivite = itemView.findViewById<TextView>(R.id.txtSecteurActivite)

        fun bind(entreprise: Entreprise) {
            txtNomEntreprise.text = entreprise.nom
            txtSecteurActivite.text = entreprise.secteurActivite
        }
    }
}