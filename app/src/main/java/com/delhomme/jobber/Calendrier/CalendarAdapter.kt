package com.delhomme.jobber.Calendrier

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarAdapter(private val days: List<Date>, private val events: List<Event>) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayNumber : TextView = view.findViewById(R.id.dayText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_day_layout, parent, false)
        return CalendarViewHolder(view)
    }
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = days[position]
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        holder.dayNumber.text = dateFormat.format(day)

        // Highlight if it's today
        if (dateFormat.format(day) == dateFormat.format(Date())) {
            holder.dayNumber.setBackgroundColor(Color.YELLOW) // Example color
        } else {
            holder.dayNumber.setBackgroundColor(Color.TRANSPARENT)
        }
    }


    override fun getItemCount(): Int = days.size
}