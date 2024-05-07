package com.delhomme.jobber

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DashboardFragment()
            1 -> CandidatureListFragment()
            2 -> AppelListFragment()
            3 -> ContactListFragment()
            4 -> EntretienListFragment()
            5 -> EntrepriseListFragment()
            else -> Fragment()
        }
    }
}
