package com.delhomme.jobber.Calendrier

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.DataRepository
import com.delhomme.jobber.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FragmentCalendrier : Fragment() {
    private lateinit var hoursRecyclerView: RecyclerView
    private lateinit var currentDayTextView: TextView
    private lateinit var eventsRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calendrier, container, false)
        hoursRecyclerView = view.findViewById(R.id.hoursRecyclerView)
        currentDayTextView = view.findViewById(R.id.currentDay)

        // Set the layout manager
        hoursRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        loadEventsAndHours()
        setupDayView()

        view.findViewById<Button>(R.id.prevDayButton).setOnClickListener {
            adjustDay(-1)
        }
        view.findViewById<Button>(R.id.nextDayButton).setOnClickListener {
            adjustDay(1)
        }
    }
    private fun setupRecyclerViews() {
        hoursRecyclerView = view?.findViewById(R.id.hoursRecyclerView) ?: throw IllegalStateException("View should not be null")
        eventsRecyclerView = view?.findViewById(R.id.eventsRecyclerView) ?: throw IllegalStateException("View should not be null")

        hoursRecyclerView.layoutManager = LinearLayoutManager(context)
        eventsRecyclerView.layoutManager = LinearLayoutManager(context)

        hoursRecyclerView.adapter = HoursAdapter(createHoursList())
        eventsRecyclerView.adapter = EventAdapter(DataRepository(requireContext()).getEvents())
    }

    private fun createHoursList(): List<String> {
        return List(24) { hour -> "${hour}:00"}
    }

    private fun adjustDay(dayDelta: Int) {
        val dateFormat = SimpleDateFormat("EEE, d MMM, yyyy", Locale.FRENCH)
        val cal = Calendar.getInstance().apply {
            time = dateFormat.parse(currentDayTextView.text.toString()) ?: return
            add(Calendar.DAY_OF_YEAR, dayDelta)
        }
        currentDayTextView.text = dateFormat.format(cal.time)
        loadEventsAndHours()
    }

    private fun updateViewMode(viewMode: String) {
        when(viewMode) {
            "Jour" -> setupDayView()
            "5 jours" -> setupFiveDayView()
            "Mois" -> setupMonthView()
        }
    }
    private fun setupDayView() {
        val dateString = currentDayTextView.text.toString()
        if (dateString.isNotEmpty()) {
            try {
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("EEE, d MMMM, yyyy", Locale.FRENCH)
                val startDate = dateFormat.parse(dateString)
                calendar.time = startDate

                val events = createSampleEvents() // Make sure this returns correctly timed events
                val hoursEvents = List(24) { hour ->
                    HourEvent("$hour:00", events.filter {
                        val eventCal = Calendar.getInstance()
                        eventCal.time = Date(it.startTime)
                        eventCal.get(Calendar.HOUR_OF_DAY) == hour
                    })
                }

                hoursRecyclerView.adapter = CombinedHoursEventsAdapter(hoursEvents)
            } catch (e: ParseException) {
                Log.e("FragmentCalendrier", "Unparseable date: $dateString", e)
            }
        } else {
            Log.e("FragmentCalendrier", "Date string is empty")
        }
    }


    private fun setupFiveDayView() {
        try {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("EEE, d MMM, yyyy", Locale.FRENCH)
            val dateString = currentDayTextView.text.toString()
            val startDate = dateFormat.parse(dateString)
            if (startDate != null) {
                calendar.time = startDate
                val days = mutableListOf<Date>()
                for (i in 0 until 5) {
                    days.add(calendar.time)
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                hoursRecyclerView.adapter = FiveDaysAdapter(days)
            } else {
                Log.e("FragmentCalendrier", "Could not parse the date: $dateString")
            }
        } catch (e: ParseException) {
            Log.e("FragmentCalendrier", "Unparseable date...", e)
        }
    }


    private fun setupMonthView() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, d MMM, yyyy", Locale.FRENCH)
        val startDate = dateFormat.parse(currentDayTextView.text.toString()) ?: return

        calendar.time = startDate
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val maxDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val days = mutableListOf<Date>()

        for (i in 1..maxDayInMonth) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        hoursRecyclerView.adapter = MonthAdapter(days)
    }

    private fun createSampleEvents(): List<Event> {
        return listOf(
            Event(
                id = "1",
                title = "Entretien avec ABC Corp",
                description = "Discussion du role d'ingénieur logiciel",
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis() + 3600000,
                type = EventType.Entretien,
                relatedId = "001",
                entrepriseId = "E001"
            ),

            Event(
                id = "2",
                title = "Follow-up Call with XYZ Ltd",
                description = "Discussion on project terms",
                startTime = System.currentTimeMillis() + 7200000, // plus two hours
                endTime = System.currentTimeMillis() + 10800000, // plus three hours
                type = EventType.Appel,
                relatedId = "002",
                entrepriseId = "E002"
            )
        )
    }

    private fun loadEventsAndHours() {
        val events = DataRepository(requireContext()).getEvents()
        val hoursEvents = List(24) { hour ->
            HourEvent("$hour:00", events.filter {
                val eventCal = Calendar.getInstance()
                eventCal.time = Date(it.startTime)
                eventCal.get(Calendar.HOUR_OF_DAY) == hour
            })
        }
        hoursRecyclerView.adapter = CombinedHoursEventsAdapter(hoursEvents)
    }

}
