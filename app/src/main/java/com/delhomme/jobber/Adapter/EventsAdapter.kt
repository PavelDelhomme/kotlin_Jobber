package com.delhomme.jobber.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Model.Evenement
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventsAdapter(private val events: List<Evenement>) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
        Log.d("EventsAdapter", "Binding event at position $position: ${event.title}")
    }

    override fun getItemCount() = events.size

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.eventTitle)
        private val dateText: TextView = itemView.findViewById(R.id.eventDate)

        fun bind(event: Evenement) {
            titleText.text = event.title
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
            dateText.text = "${dateFormat.format(Date(event.startTime))} - ${dateFormat.format(Date(event.endTime))}"
            Log.d("EventsAdapter", "event : $event")
            Log.d("EventsAdapter", "event.startTime : ${event.startTime}")
            Log.d("EventsAdapter", "event.endTime : ${event.endTime}")
            Log.d("EventsAdapter", "event.title : ${event.title}")
        }
    }
}
