package com.delhomme.jobber.Calendrier

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FragmentCalendrier : Fragment() {
    private lateinit var hoursRecyclerView: RecyclerView
    private lateinit var currentDayTextView: TextView

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

        val today = Calendar.getInstance()
        currentDayTextView.text = SimpleDateFormat("EEE, d MMM, yyyy", Locale.FRENCH).format(today.time)
        view.findViewById<Button>(R.id.prevDayButton).setOnClickListener {
            changeDay(-1)
        }
        view.findViewById<Button>(R.id.nextDayButton).setOnClickListener {
            changeDay(1)
        }

        view.findViewById<Spinner>(R.id.viewSelector).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                updateViewMode(parent.getItemAtPosition(position).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val viewModes = arrayOf("Jour", "5 jours", "Mois")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, viewModes)
        view.findViewById<Spinner>(R.id.viewSelector).adapter = spinnerAdapter

        updateViewMode("Jour")
    }

    private fun changeDay(dayChange: Int) {
        val dateString = currentDayTextView.text.toString()
        if (!dateString.isEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("EEE., d, MM, yyyy", Locale.FRENCH)
                val cal = Calendar.getInstance()
                cal.setTime(dateFormat.parse(dateString))
                cal.add(Calendar.DAY_OF_YEAR, dayChange)
                currentDayTextView.setText(dateFormat.format(cal.time))
            } catch (e: ParseException) {
                Log.e("CalendarError", "Error parsing date : $dateString\n$e")

            }
        }
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
        //hoursRecyclerView.adapter = HoursAdapter()
        if (dateString.isNotEmpty()) {
            try {
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("EEE, d MMMM, yyyy", Locale.FRENCH)
                val startDate = dateFormat.parse(dateString)
                calendar.time = startDate

                val events = createSampleEvents()

                val hours = List(24) { index -> HourEvent(index.toString() + ":00", events.filter { it.startTime <= index && it.endTime > index })}

                hoursRecyclerView.adapter = CombinedHoursEventsAdapter(hours)
                EventAdapter(events)
                val days = mutableListOf<Date>()
                for (i in 0 until 1) {
                    days.add(calendar.time)
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
            } catch (e: ParseException) {
                Log.e("FragmentCalendrier", "Unparseable date : $dateString", e)
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

}
