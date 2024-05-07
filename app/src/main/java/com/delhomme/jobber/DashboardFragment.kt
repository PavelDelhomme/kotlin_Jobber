// DashboardFragment.kt
package com.delhomme.jobber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class DashboardFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gonfler le layout du fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Configure les cartes
        val cardCandidature = view.findViewById<CardView>(R.id.cardCandidature)
        val cardEntretien = view.findViewById<CardView>(R.id.cardEntretien)

        cardCandidature.setOnClickListener {
            // Logique d'ouverture de la liste des candidatures
        }

        cardEntretien.setOnClickListener {
            // Logique d'ouverture de la liste des entretiens
        }

        return view
    }
}
