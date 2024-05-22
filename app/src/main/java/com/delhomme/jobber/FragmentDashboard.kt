package com.delhomme.jobber

import android.annotation.SuppressLint
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
import com.delhomme.jobber.Entretien.DetailsEntretienActivity
import com.delhomme.jobber.Entretien.EditEntretienActivity
import com.delhomme.jobber.Entretien.adapter.EntretienAdapter

class FragmentDashboard : Fragment() {

    private lateinit var dataRepository: DataRepository
    private lateinit var adapter: EntretienAdapter

    //private lateinit var recyclerViewUpcomingInterviews: RecyclerView
    private lateinit var btnPrevious: Button
    private lateinit var btnToday: Button
    private lateinit var btnNext: Button
    private lateinit var webViewCandidaturePerPlateforme: WebView
    private lateinit var webViewCandidaturePerTypePoste: WebView
    private lateinit var webViewCandidaturePerEntreprise: WebView
    private lateinit var webViewAppelPer7Days: WebView
    private lateinit var webViewCandidaturePer7Days: WebView
    private lateinit var webViewRelancePer7Days: WebView
    private lateinit var webViewRelancePerPlateforme: WebView
    private lateinit var webViewEntretienPer7Days: WebView
    //private lateinit var webViewEntretienPerType: WebView
    private lateinit var webViewEntretienPerStyle: WebView
    private lateinit var webViewCandidaturePerLocation: WebView
    private lateinit var webViewCandidaturePerState: WebView

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

        //recyclerViewUpcomingInterviews = view.findViewById(R.id.recyclerViewUpcomingInterviews)
        btnPrevious = view.findViewById(R.id.btnPrevious)
        btnToday = view.findViewById(R.id.btnToday)
        btnNext = view.findViewById(R.id.btnNext)

        webViewCandidaturePer7Days = view.findViewById(R.id.webViewCandidaturePer7Days)
        webViewCandidaturePerPlateforme = view.findViewById(R.id.webViewCandidaturePerPlateforme)
        webViewCandidaturePerTypePoste = view.findViewById(R.id.webViewCandidaturePerTypePoste)
        webViewCandidaturePerEntreprise = view.findViewById(R.id.webViewCandidaturePerEntreprise)
        webViewCandidaturePerLocation = view.findViewById(R.id.webViewCandidaturePerLocation)
        webViewCandidaturePerState = view.findViewById(R.id.webViewCandidaturePerState)
        webViewRelancePer7Days = view.findViewById(R.id.webViewRelancePer7Days)
        webViewRelancePerPlateforme = view.findViewById(R.id.webViewRelancePerPlateforme)
        webViewAppelPer7Days = view.findViewById(R.id.webViewAppelPer7Days)
        webViewEntretienPer7Days = view.findViewById(R.id.webViewEntretienPer7Days)
        //webViewEntretienPerType = view.findViewById(R.id.webViewEntretienPerType)
        //webViewEntretienPerStyle = view.findViewById(R.id.webViewEntretienPerStyle)


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
        //recyclerViewUpcomingInterviews.layoutManager = LinearLayoutManager(context)
        //recyclerViewUpcomingInterviews.adapter = adapter
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
        val graphCandidaturePer7Days = dataRepository.getCandidaturesLast7Days(dayOffset)
        val graphCandidaturePerPlateforme = dataRepository.getCandidaturesPerPlateforme()
        val graphCandidaturePerTypePoste = dataRepository.getCandidaturesPerTypePoste()
        val graphCandidaturePerEntreprise = dataRepository.getCandidaturesPerCompany()
        val graphCandidaturePerLocation = dataRepository.getCandidaturesPerLocation()
        val graphCandidaturePerState = dataRepository.getCandidaturesPerState()

        val graphRelancePer7Days = dataRepository.getRelancesLast7DaysDatas(dayOffset)
        val graphRelancePerPlateforme = dataRepository.getRelancesPerPlateforme()

        val graphAppelPer7Days = dataRepository.getRelancesLast7DaysDatas(dayOffset)

        val graphEntretienPer7Days = dataRepository.getEntretiensLast7Days(dayOffset)
        //val graphEntretienPerType = dataRepository.getEntretiensPerTypeDatas()
        //val graphEntretienPerStyle = dataRepository.getEntretiensPerStyleDatas()
        setupWebView(webViewCandidaturePer7Days, graphCandidaturePer7Days)

        Log.d("FragmentDashboard", "Loading graphCandidaturePerPlateforme : $graphCandidaturePerPlateforme")
        setupWebView(webViewCandidaturePerPlateforme, graphCandidaturePerPlateforme)

        Log.d("FragmentDashboard", "Loading graphCandidaturePerTypePoste : $graphCandidaturePerTypePoste")
        setupWebView(webViewCandidaturePerTypePoste, graphCandidaturePerTypePoste)

        Log.d("FragmentDashboard", "Loading graphCandidaturePerEntreprise : $graphCandidaturePerEntreprise")
        setupWebView(webViewCandidaturePerEntreprise, graphCandidaturePerEntreprise)

        Log.d("FragmentDashboard", "Loading graphCandidaturePerLocation : $graphCandidaturePerLocation")
        setupWebView(webViewCandidaturePerLocation, graphCandidaturePerLocation)

        Log.d("FragmentDashboard", "Loading graphCandidaturePerState : $graphCandidaturePerState")
        setupWebView(webViewCandidaturePerState, graphCandidaturePerState)

        Log.d("FragmentDashboard", "Loading graphCandidaturePerEntreprise : $graphCandidaturePerEntreprise")
        setupWebView(webViewCandidaturePerEntreprise, graphCandidaturePerEntreprise)

        Log.d("FragmentDashboard", "Loading graphRelancePer7Days : $graphRelancePer7Days")
        setupWebView(webViewRelancePer7Days, graphRelancePer7Days)

        Log.d("FragmentDashboard", "Loading graphRelancePerPlateforme : $graphRelancePerPlateforme")
        setupWebView(webViewRelancePerPlateforme, graphRelancePerPlateforme)

        Log.d("FragmentDashboard", "Loading graphAppelPer7Days : $graphAppelPer7Days")
        setupWebView(webViewAppelPer7Days, graphAppelPer7Days)

        //Log.d("FragmentDashboard", "Loading graphEntretienPer7Days : $graphEntretienPer7Days")
        //setupWebView(webViewEntretienPer7Days, graphEntretienPer7Days)

        //Log.d("FragmentDashboard", "Loading graphEntretienPerType : $graphEntretienPerType")
        //setupWebView(webViewEntretienPerType, graphEntretienPerType)

        //Log.d("FragmentDashboard", "Loading graphEntretienPerStyle : $graphEntretienPerStyle")
        //setupWebView(webViewEntretienPerStyle, graphEntretienPerStyle)

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(graph: WebView, graphData: String) {
        graph.settings.javaScriptEnabled = true
        graph.loadDataWithBaseURL(null, graphData, "text/html", "UTF-8", null)
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
