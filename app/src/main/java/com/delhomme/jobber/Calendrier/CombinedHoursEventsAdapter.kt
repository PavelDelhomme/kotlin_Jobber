package com.delhomme.jobber.Calendrier

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CombinedHoursEventsAdapter(private val hoursEvents: List<HourEvent>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hourEvent = hoursEvents[position]
    }

    override fun getItemCount() = hoursEvents.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                // Handle the click event
            }
        }
    }
}