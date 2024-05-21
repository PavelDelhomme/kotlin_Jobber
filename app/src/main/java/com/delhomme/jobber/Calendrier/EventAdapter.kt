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

class EventAdapter(private var events: List<Event>) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.eventTitle)
        val description: TextView = view.findViewById(R.id.eventDescription)
        val type: TextView = view.findViewById(R.id.eventType)
        val startTime: TextView = view.findViewById(R.id.eventStartTime)
        val endTime: TextView = view.findViewById(R.id.eventEndTime)

        fun bind(event: Event) {
            title.text = event.title
            description.text = event.description
            type.text = event.type.name
            startTime.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date(event.startTime))
            endTime.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date(event.endTime))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position] // Just directly access the event
        holder.bind(event)
    }

    override fun getItemCount(): Int = events.size
}
