package com.delhomme.jobber.Calendrier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Adapter.EventsAdapter
import com.delhomme.jobber.Model.Evenement
import com.delhomme.jobber.Utils.DataRepository
import com.delhomme.jobber.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentCalendrier : Fragment() {
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var currentDayTextView: TextView
    private lateinit var prevDayButton: Button
    private lateinit var nextDayButton: Button
    private var currentDate = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendrier, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView)
        currentDayTextView = view.findViewById(R.id.currentDay)
        prevDayButton = view.findViewById(R.id.prevDayButton)
        nextDayButton = view.findViewById(R.id.nextDayButton)

        updateDateInView()
        setupDayView()

        prevDayButton.setOnClickListener {
            changeDay(-1)
        }
        nextDayButton.setOnClickListener {
            changeDay(1)
        }

        // currentDayTextView.text = SimpleDateFormat("EEE, d MMM, yyyy", Locale.FRENCH).format(System.currentTimeMillis())
    }

    private fun changeDay(days: Int) {
        currentDate.add(Calendar.DAY_OF_YEAR, days)
        updateDateInView()
        setupDayView()
    }

    private fun updateDateInView() {
        val dateFormat = SimpleDateFormat("EEE, d, MMM, yyyy", Locale.FRENCH)
        currentDayTextView.text = dateFormat.format(currentDate.time)
    }
    private fun setupDayView() {
        val events = DataRepository(requireContext()).getEventsOn(currentDate.time)
        eventsRecyclerView.layoutManager = LinearLayoutManager(context)
        eventsRecyclerView.adapter = EventsAdapter(events)
        scrollToNearestEvent(events)
    }

    private fun scrollToNearestEvent(events: List<Evenement>) {
        val nearestIndex = findNearestEventIndex(events)
        if (nearestIndex != -1) {
            eventsRecyclerView.scrollToPosition(nearestIndex)
        }
    }

    private fun findNearestEventIndex(events: List<Evenement>): Int {
        val now = System.currentTimeMillis()
        return events.indexOfFirst { it.startTime > now }
    }

}
