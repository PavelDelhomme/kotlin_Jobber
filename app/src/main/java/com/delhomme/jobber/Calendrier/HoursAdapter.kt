package com.delhomme.jobber.Calendrier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R


class HoursAdapter(private val hours: List<String>) : RecyclerView.Adapter<HoursAdapter.HourViewHolder>() {
    class HourViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val hourText: TextView = view.findViewById(R.id.hourText)

        fun bind(hour: String) {
            hourText.text = hour
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hour_item_layout, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.bind(hours[position])
    }

    override fun getItemCount() = hours.size
}

