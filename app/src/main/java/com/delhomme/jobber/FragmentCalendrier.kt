package com.delhomme.jobber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class FragmentCalendrier : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.title = "Calendrier"
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendrier, container, false)
    }
}
