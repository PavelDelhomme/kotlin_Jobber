package com.delhomme.jobber

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

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

