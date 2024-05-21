package com.delhomme.jobber.Calendrier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FragmentCalendrier : Fragment() {

    private lateinit var compactCalendarView: CompactCalendarView
    private val dateFormat = SimpleDateFormat("dd/MM/yyy HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "Calendrier"
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_calendrier, container, false)
        compactCalendarView = view.findViewById(R.id.compactcalendar_view)

        compactCalendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                displayEventsForDay(dateClicked)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                TODO("Not yet implemented")
            }
        })

        return view
    }

    private fun displayEventsForDay(date: Date?) {
        val events = compactCalendarView.getEvents(date)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadEvents()
    }

    private fun loadEvents() {
        val dataRepository = DataRepository(this)
        val events = dataRepository.getEvents()
        events.forEach { event ->
            val color = when (event.type) {
                EventType.Candidature -> Color.Yellow
                EventType.Entretien -> Color.Blue
                EventType.Relance -> Color.Red
                EventType.Appel -> Color.Green
            }
            val compactEvent = com.github.sundeepk.compactcalendarview.domain.Event(color, event.startTime, event)
            compactCalendarView.addEvent(compactEvent)
        }
    }
}
