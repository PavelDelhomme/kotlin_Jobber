package com.delhomme.jobber

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.delhomme.jobber.Activity.Appel.AddAppelActivity
import com.delhomme.jobber.Activity.Candidature.AddCandidatureActivity
import com.delhomme.jobber.Activity.Contact.AddContactActivity
import com.delhomme.jobber.Activity.Entretien.AddEntretienActivity
import com.delhomme.jobber.Activity.SignUser.LoginActivity
import com.delhomme.jobber.Api.DjangoApi.ApiService
import com.delhomme.jobber.Api.DjangoApi.DataSyncManager
import com.delhomme.jobber.Api.DjangoApi.RetrofitClient
import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import com.delhomme.jobber.Api.Repository.SearchDataRepository
import com.delhomme.jobber.Fragment.FragmentAppels
import com.delhomme.jobber.Fragment.FragmentCalendrier
import com.delhomme.jobber.Fragment.FragmentCandidatures
import com.delhomme.jobber.Fragment.FragmentContacts
import com.delhomme.jobber.Fragment.FragmentDashboard
import com.delhomme.jobber.Fragment.FragmentEntreprises
import com.delhomme.jobber.Fragment.FragmentEntretiens
import com.delhomme.jobber.Fragment.FragmentNotifications
import com.delhomme.jobber.Fragment.FragmentRelances
import com.delhomme.jobber.Fragment.SearchResultsFragment
import com.delhomme.jobber.Notification.NotificationReceiver
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.Calendar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val REQUEST_NOTIFICATION_PERMISSION = 1001
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var searchDataRepository: SearchDataRepository
    private lateinit var dataSyncManager: DataSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchDataRepository = SearchDataRepository(this)
        LocalStorageManager.initialize(this)

        val apiService = RetrofitClient.createService(ApiService::class.java)
        dataSyncManager = DataSyncManager(apiService, this)

        setupUI(savedInstanceState)
        checkPermissionsAndSetupNotifications()

        if (LocalStorageManager.isTokenValid()) {
            Log.d("MainActivity", "MainActivity : Token is valid sync datas")
            dataSyncManager.syncData()
        } else {
            Log.d("MainActivity", "MainActivity : Token is not valid, redirect to login")
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun setupUI(savedInstanceState: Bundle?) {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        findViewById<FloatingActionButton>(R.id.fabMenuJobber).setOnClickListener { view ->
            showPopupMenu(view)
        }

        if (savedInstanceState == null) {
            replaceFragment(FragmentDashboard())
        }
    }

    private fun checkPermissionsAndSetupNotifications() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
        } else {
            configureNotificationAlarm()
        }
    }

    private fun configureNotificationAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = PendingIntent.getBroadcast(this, 0, Intent(this, NotificationReceiver::class.java), PendingIntent.FLAG_IMMUTABLE)
        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
    }

    private fun showPopupMenu(view: View) {
        PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.menu_add_items, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_add_candidature -> startActivity(Intent(this@MainActivity, AddCandidatureActivity::class.java))
                    R.id.menu_add_contact -> startActivity(Intent(this@MainActivity, AddContactActivity::class.java))
                    R.id.menu_add_appel -> startActivity(Intent(this@MainActivity, AddAppelActivity::class.java))
                    R.id.menu_add_entretien -> startActivity(Intent(this@MainActivity, AddEntretienActivity::class.java))
                    else -> false
                }
                true
            }
            show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        (menu.findItem(R.id.action_search).actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText)
                return true
            }
        })
        return true
    }

    private fun performSearch(query: String?) {
        query?.let {
            if (it.isNotEmpty()) {
                val results = searchDataRepository.search(it)
                displayResults(results)
            } else {
                displayDefaultState()
            }
        }
    }

    private fun displayResults(results: List<Any>) {
        replaceFragment(SearchResultsFragment.newInstance(results))
    }

    private fun displayDefaultState() {
        replaceFragment(FragmentDashboard())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> replaceFragment(FragmentDashboard())
            R.id.nav_candidatures -> replaceFragment(FragmentCandidatures())
            R.id.nav_contact_list -> replaceFragment(FragmentContacts())
            R.id.nav_appel_list -> replaceFragment(FragmentAppels())
            R.id.nav_relances -> replaceFragment(FragmentRelances())
            R.id.nav_entretiens -> replaceFragment(FragmentEntretiens())
            R.id.nav_entreprises -> replaceFragment(FragmentEntreprises())
            R.id.nav_calendrier -> replaceFragment(FragmentCalendrier())
            R.id.nav_notifications -> replaceFragment(FragmentNotifications())
        }
        drawerLayout.closeDrawer(navView)
        return true
    }
}
