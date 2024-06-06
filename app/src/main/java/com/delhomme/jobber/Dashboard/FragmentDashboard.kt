package com.delhomme.jobber.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.delhomme.jobber.Api.Repository.AppelDataRepository
import com.delhomme.jobber.Api.Repository.CandidatureDataRepository
import com.delhomme.jobber.Api.Repository.EntretienDataRepository
import com.delhomme.jobber.Api.Repository.RelanceDataRepository
import com.delhomme.jobber.R

class FragmentDashboard : Fragment() {
    private lateinit var candidatureDataRepository: CandidatureDataRepository
    private lateinit var appelDataRepository: AppelDataRepository
    private lateinit var entretienDataRepository: EntretienDataRepository
    private lateinit var relanceDataRepository: RelanceDataRepository

    private lateinit var webViewCandidaturePer7Days: WebView
    private lateinit var webViewCandidaturePerPlateforme: WebView
    private lateinit var webViewCandidaturePerTypePoste: WebView
    private lateinit var webViewCandidaturePerEntreprise: WebView
    private lateinit var webViewCandidaturePerLocation: WebView
    private lateinit var webViewCandidaturePerState: WebView
    private lateinit var webViewAppelPer7Days: WebView
    private lateinit var webViewEntretiensPerType: WebView
    private lateinit var webViewRelancePerPlateforme: WebView
    private lateinit var webViewEntretiensPer7Days: WebView
    private lateinit var webViewEntretiensPerStyle: WebView

    private lateinit var btnPrevious: Button
    private lateinit var btnToday: Button
    private lateinit var btnNext: Button

    private var dayOffset = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Dashboard"

        candidatureDataRepository = CandidatureDataRepository(requireContext())
        appelDataRepository = AppelDataRepository(requireContext())
        entretienDataRepository = EntretienDataRepository(requireContext())
        relanceDataRepository = RelanceDataRepository(requireContext())

        setupViews(view)
        setupGraphNavigation()
        loadGraphData()
    }

    private fun setupViews(view: View) {
        btnPrevious = view.findViewById(R.id.btnPrevious)
        btnToday = view.findViewById(R.id.btnToday)
        btnNext = view.findViewById(R.id.btnNext)

        webViewCandidaturePer7Days = view.findViewById(R.id.webViewCandidaturePer7Days)
        webViewCandidaturePerPlateforme = view.findViewById(R.id.webViewCandidaturePerPlateforme)
        webViewCandidaturePerTypePoste = view.findViewById(R.id.webViewCandidaturePerTypePoste)
        webViewCandidaturePerEntreprise = view.findViewById(R.id.webViewCandidaturePerEntreprise)
        webViewCandidaturePerLocation = view.findViewById(R.id.webViewCandidaturePerLocation)
        webViewCandidaturePerState = view.findViewById(R.id.webViewCandidaturePerState)
        webViewAppelPer7Days = view.findViewById(R.id.webViewAppelPer7Days)
        webViewEntretiensPerType = view.findViewById(R.id.webViewEntretiensPerType)
        webViewRelancePerPlateforme = view.findViewById(R.id.webViewRelancePerPlateforme)
        webViewEntretiensPer7Days = view.findViewById(R.id.webViewEntretiensPer7Days)
        webViewEntretiensPerStyle = view.findViewById(R.id.webViewEntretiensPerStyle)

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
        setupWebView(webViewCandidaturePer7Days, candidatureDataRepository.getCandidaturesLast7Days(dayOffset))
        setupWebView(webViewCandidaturePerPlateforme, candidatureDataRepository.getCandidaturesPerPlateforme(dayOffset))
        setupWebView(webViewCandidaturePerTypePoste, candidatureDataRepository.getCandidaturesPerTypePoste(dayOffset))
        setupWebView(webViewCandidaturePerPlateforme, candidatureDataRepository.getCandidaturesPerPlateforme(dayOffset))
        setupWebView(webViewCandidaturePerTypePoste, candidatureDataRepository.getCandidaturesPerTypePoste(dayOffset))
        setupWebView(webViewCandidaturePerEntreprise, candidatureDataRepository.getCandidaturesPerCompany())
        setupWebView(webViewCandidaturePerLocation, candidatureDataRepository.getCandidaturesPerLocation())
        setupWebView(webViewCandidaturePerState, candidatureDataRepository.getCandidaturesPerState())
        setupWebView(webViewAppelPer7Days, appelDataRepository.getAppelsLast7DaysDatas(dayOffset))
        setupWebView(webViewEntretiensPerType, entretienDataRepository.getEntretiensPerTypeDatas())
        setupWebView(webViewRelancePerPlateforme, relanceDataRepository.getRelancesPerPlateforme())
        setupWebView(webViewEntretiensPer7Days, entretienDataRepository.getEntretiensLast7Days(dayOffset))
        setupWebView(webViewEntretiensPerStyle, entretienDataRepository.getEntretiensPerStyleDatas())
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(webView: WebView, htmlData: String) {
        webView.settings.javaScriptEnabled = true
        webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
    }

    override fun onResume() {
        super.onResume()
        loadGraphData()
    }
}
