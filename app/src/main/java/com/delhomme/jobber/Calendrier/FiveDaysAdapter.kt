package com.delhomme.jobber.Calendrier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FiveDaysAdapter(private val days: List<Date>) : RecyclerView.Adapter<FiveDaysAdapter.DayViewHolder>() {
    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayText: TextView = view.findViewById(R.id.dayText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_day_layout, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayFormat = SimpleDateFormat("EEE dd", Locale.FRENCH)
        val day = days[position]
        holder.dayText.text = dayFormat.format(day)
    }

    override fun getItemCount() = days.size
}