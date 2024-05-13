package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.adapter.EntrepriseAdapter

class EntreprisesFragment : Fragment() {

    private lateinit var dataRepository: DataRepository
    private lateinit var entreprisesAdapter: EntrepriseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_entreprises, container, false)

        dataRepository = DataRepository(requireContext())
        entreprisesAdapter = EntrepriseAdapter(dataRepository.loadEntreprises()) {
            // Ici, vous pouvez gérer le clic sur un élément pour ouvrir l'activité de détail
            val intent = Intent(context, EntrepriseDetailActivity::class.java)
            intent.putExtra("ENTREPRISE_ID", it.id)
            startActivity(intent)
        }

        view.findViewById<RecyclerView>(R.id.rvEntreprises).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = entreprisesAdapter
        }

        return view
    }
}
