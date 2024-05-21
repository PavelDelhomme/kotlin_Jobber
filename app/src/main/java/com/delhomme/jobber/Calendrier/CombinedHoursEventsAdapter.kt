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

class CombinedHoursEventsAdapter(private val hoursEvents: List<HourEvent>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_HOUR = 0
        private const val TYPE_EVENT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HOUR -> HourViewHolder(inflater.inflate(R.layout.hour_item_layout, parent, false))
            TYPE_EVENT -> EventViewHolder(inflater.inflate(R.layout.item_event, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hourEvent = hoursEvents[position]
        when (holder) {
            is HourViewHolder -> holder.bind(hourEvent.hour)
            is EventViewHolder -> {
                if (hourEvent.events.isNotEmpty()) {
                    holder.bind(HourEvent.events)

                }
            }
        }
    }

    override fun getItemCount(): Int = hoursEvents.size

    override fun getItemViewType(position: Int): Int {
        return if (hoursEvents[position].events.isEmpty()) TYPE_HOUR else TYPE_EVENT
    }

    class HourViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val hourText: TextView = view.findViewById(R.id.hourText)
        fun bind(hour: String) {
            hourText.text = hour
        }
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.eventTitle)
        private val startTime: TextView = view.findViewById(R.id.eventStartTime)
        private val endTime: TextView = view.findViewById(R.id.eventEndTime)

        fun bind(events: List<Event>) {
            if (events.isNotEmpty()) {
                val event = events[0]
                title.text = event.title
                startTime.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(event.startTime))
                endTime.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(event.endTime))
            }
        }
    }
}
