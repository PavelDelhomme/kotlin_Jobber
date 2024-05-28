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
import android.widget.Toast
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
import com.delhomme.jobber.Api.DjangoApi.ApiResponse
import com.delhomme.jobber.Api.DjangoApi.RetrofitClient
import com.delhomme.jobber.Api.DjangoApi.TokenResponse
import com.delhomme.jobber.Api.DjangoApi.TokenService
import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import com.delhomme.jobber.Api.UserProfileApi
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
import com.delhomme.jobber.Utils.DataRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val REQUEST_NOTIFICATION_PERMISSION = 1001

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var dataRepository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LocalStorageManager.initialize(this)
        syncDataWithServer()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
        } else {
            // triggerTestNotification()
            configureNotificationAlarm()
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Chargez le fragment initial (par exemple, le Dashboard)
        if (savedInstanceState == null) {
            replaceFragment(FragmentDashboard())
        }

        // Configuration du Floating Action Button
        val fabMenuJobber = findViewById<FloatingActionButton>(R.id.fabMenuJobber)
        fabMenuJobber.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_add_items, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_add_candidature -> {
                    startActivity(Intent(this, AddCandidatureActivity::class.java))
                    true
                }
                R.id.menu_add_contact -> {
                    startActivity(Intent(this, AddContactActivity::class.java))
                    true
                }
                R.id.menu_add_appel -> {
                    startActivity(Intent(this, AddAppelActivity::class.java))
                    true
                }
                R.id.menu_add_entretien -> {
                    startActivity(Intent(this, AddEntretienActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun configureNotificationAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, NotificationReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 18)  // Set the alarm time to 6 PM
            set(Calendar.MINUTE, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                configureNotificationAlarm()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                replaceFragment(FragmentDashboard())
            }
            R.id.nav_candidatures -> {
                replaceFragment(FragmentCandidatures())
            }
            R.id.nav_contact_list -> {
                replaceFragment(FragmentContacts())
            }
            R.id.nav_appel_list -> {
                replaceFragment(FragmentAppels())
            }
            R.id.nav_relances -> {
                replaceFragment(FragmentRelances())
            }
            R.id.nav_entretiens -> {
                replaceFragment(FragmentEntretiens())
            }
            R.id.nav_entreprises -> {
                replaceFragment(FragmentEntreprises())
            }
            R.id.nav_calendrier -> {
                replaceFragment(FragmentCalendrier())
            }
            R.id.nav_notifications -> {
                replaceFragment(FragmentNotifications())
            }
        }
        drawerLayout.closeDrawer(navView)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu!!.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        if (!query.isNullOrEmpty()) {
            val results = dataRepository.search(query)
            displayResults(results)
        } else {
            displayDefaultState()
        }

    }

    private fun displayResults(results: List<Any>) {
        val searchResultsFragment = SearchResultsFragment.newInstance(results)
        replaceFragment(searchResultsFragment)
    }

    private fun displayDefaultState() {
        // Utiliser cette méthode pour afficher l'état par défaut de k'aookucatuib
        replaceFragment(FragmentDashboard())
    }

    fun syncDataWithServer(data: String) {
        val unsyncedData = LocalStorageManager.getData("unsynced_data_key")
        unsyncedData?.let { data ->
            val apiService = RetrofitClient.createService(UserProfileApi::class.java)
            apiService.sendDataToServer(data).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        LocalStorageManager.clearSpecificData("unsynced_data_key")
                    } else if (response.code() == 401) {
                        refreshTokenAndRetrySync(data)
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.e("MainActivity", "synDataWithServer : error: ${t.message}")
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }
    private fun refreshTokenAndRetrySync(data: String) {
        val refreshToken = LocalStorageManager.getRefreshToken()
        refreshToken?.let {
            val params = mapOf("refresh" to it)
            val tokenService = RetrofitClient.createService(TokenService::class.java)
            tokenService.refreshToken(params).enqueue(object : Callback<TokenResponse> {
                override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.accessToken?.let { newToken ->
                            LocalStorageManager.saveJWT(newToken)
                            syncDataWithServer(data)
                        }
                    } else {
                        redirectToLogin()
                    }
                }

                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    redirectToLogin()
                }
            })
        } ?: redirectToLogin()  // Si aucun refreshToken n'est trouvé, rediriger vers LoginActivity
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}
