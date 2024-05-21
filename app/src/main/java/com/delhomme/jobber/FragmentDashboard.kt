package com.delhomme.jobber

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.Entretien.DetailsEntretienActivity
import com.delhomme.jobber.Entretien.EditEntretienActivity
import com.delhomme.jobber.Entretien.adapter.EntretienAdapter

class FragmentDashboard : Fragment() {

    private lateinit var dataRepository: DataRepository
    private lateinit var adapter: EntretienAdapter

    private lateinit var recyclerViewUpcomingInterviews: RecyclerView
    private lateinit var btnPrevious: Button
    private lateinit var btnToday: Button
    private lateinit var btnNext: Button
    private lateinit var webView: WebView

    private var dayOffset = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Dashboard"

        dataRepository = DataRepository(requireContext())

        recyclerViewUpcomingInterviews = view.findViewById(R.id.recyclerViewUpcomingInterviews)
        btnPrevious = view.findViewById(R.id.btnPrevious)
        btnToday = view.findViewById(R.id.btnToday)
        btnNext = view.findViewById(R.id.btnNext)
        webView = view.findViewById(R.id.webView)

        setupUpcomingInterviews()
        setupGraphNavigation()
        loadGraphData()
    }

    private fun setupUpcomingInterviews() {
        val upcomingInterviews = dataRepository.getUpcomingInterviews(7)
        adapter = EntretienAdapter(
            upcomingInterviews,
            dataRepository,
            itemClickListener = { entretien ->
                val intent = Intent(context, DetailsEntretienActivity::class.java)
                intent.putExtra("entretienId", entretien.id)
                startActivity(intent)
            },
            deleteClickListener = { entretienId ->
                dataRepository.deleteEntretien(entretienId)
                updateUpcomingInterviews()
            },
            editClickListener = { entretienId ->
                val intent = Intent(context, EditEntretienActivity::class.java)
                intent.putExtra("entretienId", entretienId)
                startActivity(intent)
            }
        )
        recyclerViewUpcomingInterviews.layoutManager = LinearLayoutManager(context)
        recyclerViewUpcomingInterviews.adapter = adapter
    }

    private fun setupGraphNavigation() {
        btnPrevious.setOnClickListener {
            dayOffset -= 7
            loadGraphData()
        }

        btnToday.setOnClickListener {
            dayOffset = 0
            loadGraphData()
        }

        btnNext.setOnClickListener {
            dayOffset += 7
            loadGraphData()
        }
    }

    private fun loadGraphData() {
        val graphData = dataRepository.getGraphData(dayOffset)
        Log.d("FragmentDashboard", "Loading graph data: $graphData")
        webView.settings.javaScriptEnabled = true
        webView.loadDataWithBaseURL(null, graphData, "text/html", "UTF-8", null)
    }

    private fun updateUpcomingInterviews() {
        val upcomingInterviews = dataRepository.getUpcomingInterviews(7)
        adapter.updateEntretiens(upcomingInterviews)
    }

    private fun reloadData() {
        updateUpcomingInterviews()

        loadGraphData()
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }
}
