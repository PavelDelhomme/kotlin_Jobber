package com.delhomme.jobber.Calendrier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R


class HoursAdapter : RecyclerView.Adapter<HoursAdapter.HourViewHolder>() {
    private val hours = List(24) { "$it:00" } // Generate hours from 0:00 to 23:00

    class HourViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hourText: TextView = view.findViewById(R.id.hourText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hour_item_layout, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.hourText.text = hours[position]
    }

    override fun getItemCount() = hours.size
}

