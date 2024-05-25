package com.delhomme.jobber.Search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Appel.model.Appel
import com.delhomme.jobber.Calendrier.Evenement
import com.delhomme.jobber.Candidature.model.Candidature
import com.delhomme.jobber.Contact.model.Contact
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.Entreprise.model.Entreprise
import com.delhomme.jobber.Entretien.model.Entretien
import com.delhomme.jobber.R
import com.delhomme.jobber.Relance.model.Relance

class SearchResultsAdapter(
    private var sections: List<SearchSection>,
    private val dataRepository: DataRepository,
    private val onClick: (Any) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SECTION_HEADER = 0
        private const val TYPE_ITEM = 1
        // Define constants for each item type
        private const val ITEM_TYPE_CANDIDATURE = 101
        private const val ITEM_TYPE_CONTACT = 102
        private const val ITEM_TYPE_ENTREPRISE = 103
        private const val ITEM_TYPE_ENTRETIEN = 104
        private const val ITEM_TYPE_APPEL = 105
        private const val ITEM_TYPE_EVENEMENT = 106
        private const val ITEM_TYPE_RELANCE = 107
    }

    fun updateSections(newSection: List<SearchSection>) {
        val diffCallback = SectionDiffCallBack(sections, newSection)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        sections = newSection
        diffResult.dispatchUpdatesTo(this)
    }

    class SectionDiffCallBack(private val oldList: List<SearchSection>, private val newList: List<SearchSection>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].title == newList[newItemPosition].title
        }

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    class DiffCallback(private val oldList: List<Any>, private val newList: List<Any>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return if (oldItem::class == newItem::class) {
                when (oldItem) {
                    is Candidature -> oldItem.id == (newItem as Candidature).id
                    is Contact -> oldItem.id == (newItem as Contact).id
                    is Entreprise -> oldItem.nom == (newItem as Entreprise).nom
                    is Entretien -> oldItem.id == (newItem as Entretien).id
                    is Appel -> oldItem.id == (newItem as Appel).id
                    is Evenement -> oldItem.id == (newItem as Evenement).id
                    is Relance -> oldItem.id == (newItem as Relance).id
                    else -> false
                }
            } else false
        }

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return if (oldItem::class == newItem::class) {
                oldItem == newItem
            } else false
        }
    }

    override fun getItemViewType(position: Int): Int {
        var pos = 0
        for (section in sections) {
            if (position == pos) {
                return TYPE_SECTION_HEADER
            }
            pos++ // for the header
            if (position < pos + section.items.size) {
                val itemIndex = position - pos
                val item = section.items[itemIndex]
                return when (item) {
                    is Candidature -> ITEM_TYPE_CANDIDATURE
                    is Contact -> ITEM_TYPE_CONTACT
                    is Entreprise -> ITEM_TYPE_ENTREPRISE
                    is Entretien -> ITEM_TYPE_ENTRETIEN
                    is Appel -> ITEM_TYPE_APPEL
                    is Evenement -> ITEM_TYPE_EVENEMENT
                    is Relance -> ITEM_TYPE_RELANCE
                    else -> throw IllegalArgumentException("Unknown type of data")
                }
            }
            pos += section.items.size
        }
        throw IllegalArgumentException("Position out of bounds")
    }

    private fun getItemAtPosition(position: Int): Any {
        var pos = 0
        for (section in sections) {
            if (position == pos) {
                return section.title
            }
            pos++
            if (position < pos + section.items.size) {
                return section.items[position - pos]
            }
            pos += section.items.size
        }
        throw IllegalArgumentException("Position out of bounds")

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SECTION_HEADER -> {
                SectionHeaderViewHolder(inflater.inflate(R.layout.item_section_header, parent, false))
            }
            ITEM_TYPE_CANDIDATURE -> {
                CandidatureViewHolder(inflater.inflate(R.layout.item_search_candidatures, parent, false))
            }
            ITEM_TYPE_CONTACT -> {
                ContactViewHolder(inflater.inflate(R.layout.item_search_contacts, parent, false))
            }
            ITEM_TYPE_ENTREPRISE -> {
                EntrepriseViewHolder(inflater.inflate(R.layout.item_search_entreprises, parent, false))
            }
            ITEM_TYPE_ENTRETIEN -> {
                EntretienViewHolder(inflater.inflate(R.layout.item_search_entretiens, parent, false))
            }
            ITEM_TYPE_APPEL -> {
                AppelViewHolder(inflater.inflate(R.layout.item_search_appels, parent, false))
            }
            ITEM_TYPE_EVENEMENT -> {
                EvenementViewHolder(inflater.inflate(R.layout.item_search_evenements, parent, false))
            }
            ITEM_TYPE_RELANCE -> {
                RelanceViewHolder(inflater.inflate(R.layout.item_search_relances, parent, false))
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItemAtPosition(position)
        when (holder) {
            is CandidatureViewHolder -> if (item is Candidature) holder.bind(item)
            is ContactViewHolder -> if (item is Contact) holder.bind(item)
            is EntrepriseViewHolder -> if (item is Entreprise) holder.bind(item)
            is EntretienViewHolder -> if (item is Entretien) holder.bind(item)
            is AppelViewHolder -> if (item is Appel) holder.bind(item)
            is EvenementViewHolder -> if (item is Evenement) holder.bind(item)
            is RelanceViewHolder -> if (item is Relance) holder.bind(item)
            is SectionHeaderViewHolder -> if (item is String) holder.bind(item)
        }
    }


    class SectionHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title : TextView = view.findViewById(R.id.sectionTitle)

        fun bind(sectionTitle: String) {
            title.text = sectionTitle
        }
    }

    class CandidatureViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val nomPoste: TextView = view.findViewById(R.id.tvSearchCandidatureTitre)
        //private val entreprise: TextView = view.findViewById(R.id.entreprise)
        //private val date: TextView = view.findViewById(R.id.date_candidature)
        //private val etat: TextView = view.findViewById(R.id.etat)
        //private val typePoste: TextView = view.findViewById(R.id.typePoste)
        //private val plateforme: TextView = view.findViewById(R.id.plateforme)
        //private val notes: TextView = view.findViewById(R.id.tvNotesCandidature)

        fun bind(candidature: Candidature) {
            nomPoste.text = candidature.titre_offre
            //entreprise.text = candidature.entrepriseNom
            //date.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(candidature.date_candidature)
            //etat.text = candidature.state.toString()
            //typePoste.text = candidature.type_poste
            //plateforme.text = candidature.plateforme
            //notes.text = if (candidature.notes.isEmpty()) "Pas de notes" else candidature.notes
        }
    }

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val fullName: TextView = view.findViewById(R.id.tvSearchContactFullName)
        //private val company: TextView = view.findViewById(R.id.entrepriseContact)
        //private val phone: TextView = view.findViewById(R.id.telephoneContact)
        //private val email: TextView = view.findViewById(R.id.emailContact)

        fun bind(contact: Contact) {
            fullName.text = contact.getFullName()
            //company.text = contact.entrepriseNom
            //phone.text = contact.telephone
            //email.text = contact.email
        }
    }

    class EntrepriseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.tvSearchEntrepriseName)

        fun bind(entreprise: Entreprise) {
            name.text = entreprise.nom
        }
    }

    class EntretienViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //private val date: TextView = view.findViewById(R.id.dateEntretien)
        private val entreprise: TextView = view.findViewById(R.id.tvSearchEntretiensEntrepriseName)
        //private val typeEntretien: TextView = view.findViewById(R.id.typeEntretien)

        fun bind(entretien: Entretien) {
            //date.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(entretien.date_entretien)
            entreprise.text = entretien.entrepriseNom
            //typeEntretien.text = entretien.type
        }
    }
    class AppelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //private val date: TextView = view.findViewById(R.id.tvDateAppel)
        //private val objet: TextView = view.findViewById(R.id.tvObjetAppel)
        private val company: TextView = view.findViewById(R.id.tvSearchAppelsEntrepriseName)
        //private val contact: TextView = view.findViewById(R.id.tvContactAppel)
        //private val notes: TextView = view.findViewById(R.id.tvNotesAppels)


        fun bind(appel: Appel) {
            //date.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(appel.date_appel)
            //objet.text = appel.objet
            company.text = appel.entrepriseNom
            //val contactName = appel.contact_id?.let { dataRepository.getContactById(it)?.getFullName() } ?: "Unknown Contact"
            //contact.text = contactName
            //notes.text = appel.notes
        }
    }

    class EvenementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tvSearchEventTitle)
        //private val date: TextView = view.findViewById(R.id.eventDate)

        fun bind(evenement: Evenement) {
            title.text = evenement.title
            //date.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date(evenement.startTime))
        }
    }

    class RelanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //private val date: TextView = view.findViewById(R.id.dateRelance)
        private val entreprise: TextView = view.findViewById(R.id.tvSearchRelancesEntrepriseName)
        //private val candidatureRelance: TextView = view.findViewById(R.id.candidatureTitre)
        //private val plateformeRelance: TextView = view.findViewById(R.id.plateformeRelance)
        //private val notesRelance: TextView = view.findViewById(R.id.notesRelance)



        fun bind(relance: Relance) {
            //date.text = relance.date_relance.toString()
            entreprise.text = relance.entrepriseNom
            //candidatureRelance.text = relance.candidatureId.let { dataRepository.getCandidatureById(it)?.titre_offre }
                ?: "Unknown Candidature"
            //plateformeRelance.text = relance.plateformeUtilisee
            //notesRelance.text = relance.notes
        }

    }
    override fun getItemCount(): Int = sections.sumOf { it.items.size + 1 }

}