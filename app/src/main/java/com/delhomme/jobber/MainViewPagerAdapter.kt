package com.delhomme.jobber

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.delhomme.jobber.Appel.FragmentAppels
import com.delhomme.jobber.Candidature.FragmentCandidatures
import com.delhomme.jobber.Contact.FragmentContacts
import com.delhomme.jobber.Entreprise.FragmentEntreprises
import com.delhomme.jobber.Entretien.FragmentEntretiens
import com.delhomme.jobber.Relance.FragmentRelances

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 7

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentDashboard()
            1 -> FragmentCandidatures()
            2 -> FragmentContacts()
            3 -> FragmentAppels()
            4 -> FragmentEntreprises()
            5 -> FragmentEntretiens()
            6 -> FragmentRelances()
            else -> Fragment()
        }
    }
}

