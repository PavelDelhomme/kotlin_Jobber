package com.delhomme.jobber.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Api.Repository.SearchDataRepository
import com.delhomme.jobber.Model.Appel
import com.delhomme.jobber.Model.Candidature
import com.delhomme.jobber.Model.Contact
import com.delhomme.jobber.Model.Entreprise
import com.delhomme.jobber.Model.Entretien
import com.delhomme.jobber.Model.Evenement
import com.delhomme.jobber.Model.Relance
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchResultsAdapter(private val items: List<Any>,
                           private val searchDataRepository: SearchDataRepository,
                           private val onClick: (Any) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Candidature -> 0
            is Contact -> 1
            is Entreprise -> 2
            is Entretien -> 3
            is Appel -> 4
            is Evenement -> 5
            is Relance -> 6
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> CandidatureViewHolder(inflater.inflate(R.layout.item_candidature, parent, false))
            1 -> ContactViewHolder(inflater.inflate(R.layout.item_contact, parent, false))
            2 -> EntrepriseViewHolder(inflater.inflate(R.layout.item_entreprise, parent, false))
            3 -> EntretienViewHolder(inflater.inflate(R.layout.item_entretien, parent, false))
            4 -> AppelViewHolder(inflater.inflate(R.layout.item_appel, parent, false), searchDataRepository)
            5 -> EvenementViewHolder(inflater.inflate(R.layout.item_event, parent, false))
            6 -> RelanceViewHolder(inflater.inflate(R.layout.item_relance, parent, false), searchDataRepository)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is CandidatureViewHolder -> holder.bind(item as Candidature)
            is ContactViewHolder -> holder.bind(item as Contact)
            is EntrepriseViewHolder -> holder.bind(item as Entreprise)
            is EntretienViewHolder -> holder.bind(item as Entretien)
            is AppelViewHolder -> holder.bind(item as Appel)
            is EvenementViewHolder -> holder.bind(item as Evenement)
            is RelanceViewHolder -> holder.bind(item as Relance)
        }
    }

    class CandidatureViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val nomPoste: TextView = view.findViewById(R.id.nomPoste)
        private val entreprise: TextView = view.findViewById(R.id.entreprise)
        private val date: TextView = view.findViewById(R.id.date_candidature)
        private val etat: TextView = view.findViewById(R.id.etat)
        private val typePoste: TextView = view.findViewById(R.id.typePoste)
        private val plateforme: TextView = view.findViewById(R.id.plateforme)
        private val notes: TextView = view.findViewById(R.id.tvNotesCandidature)

        fun bind(candidature: Candidature) {
            nomPoste.text = candidature.titre_offre
            entreprise.text = candidature.entreprise
            date.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(candidature.date_candidature)
            etat.text = candidature.state.toString()
            typePoste.text = candidature.type_poste
            plateforme.text = candidature.plateforme
            notes.text = if (candidature.notes.isEmpty()) "Pas de notes" else candidature.notes
        }
    }

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val fullName: TextView = view.findViewById(R.id.contactFullName)
        private val company: TextView = view.findViewById(R.id.entrepriseContact)
        private val phone: TextView = view.findViewById(R.id.telephoneContact)
        private val email: TextView = view.findViewById(R.id.emailContact)

        fun bind(contact: Contact) {
            fullName.text = contact.getFullName()
            company.text = contact.entreprise
            phone.text = contact.telephone
            email.text = contact.email
        }
    }

    class EntrepriseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.tvEntrepriseName)

        fun bind(entreprise: Entreprise) {
            name.text = entreprise.nom
        }
    }

    class EntretienViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val date: TextView = view.findViewById(R.id.dateEntretien)
        private val entreprise: TextView = view.findViewById(R.id.entrepriseEntretien)
        private val typeEntretien: TextView = view.findViewById(R.id.typeEntretien)

        fun bind(entretien: Entretien) {
            date.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(entretien.date_entretien)
            entreprise.text = entretien.entrepriseNom
            typeEntretien.text = entretien.type
        }
    }
    class EvenementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.eventTitle)
        private val date: TextView = view.findViewById(R.id.eventDate)

        fun bind(evenement: Evenement) {
            title.text = evenement.title
            date.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date(evenement.start_time))
        }
    }
    class AppelViewHolder(view: View, private val searchDataRepository: SearchDataRepository) : RecyclerView.ViewHolder(view) {
        private val date: TextView = view.findViewById(R.id.tvDateAppel)
        private val objet: TextView = view.findViewById(R.id.tvObjetAppel)
        private val company: TextView = view.findViewById(R.id.tvEntrepriseAppel)
        private val contact: TextView = view.findViewById(R.id.tvContactAppel)
        private val notes: TextView = view.findViewById(R.id.tvNotesAppels)

        fun bind(appel: Appel) {
            date.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(appel.date_appel)
            objet.text = appel.objet
            company.text = appel.entrepriseNom
            val contactName = appel.contact?.let { searchDataRepository.getContactById(it)?.getFullName() } ?: "Unknown Contact"
            contact.text = contactName
            notes.text = appel.notes
        }
    }

    class RelanceViewHolder(view: View, private val searchDataRepository: SearchDataRepository) : RecyclerView.ViewHolder(view) {
        private val date: TextView = view.findViewById(R.id.dateRelance)
        private val entreprise: TextView = view.findViewById(R.id.entrepriseRelance)
        private val candidatureRelance: TextView = view.findViewById(R.id.candidatureTitre)
        private val plateformeRelance: TextView = view.findViewById(R.id.plateformeRelance)
        private val notesRelance: TextView = view.findViewById(R.id.notesRelance)

        fun bind(relance: Relance) {
            date.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(relance.date_relance)
            entreprise.text = relance.entreprise
            candidatureRelance.text = relance.candidature.let { searchDataRepository.getCandidatureById(it)?.titre_offre } ?: "Unknown Candidature"
            plateformeRelance.text = relance.plateforme_utilisee
            notesRelance.text = relance.notes
        }
    }


    override fun getItemCount(): Int = items.size

}