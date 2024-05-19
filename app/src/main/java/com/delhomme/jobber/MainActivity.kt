package com.delhomme.jobber

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.delhomme.jobber.Appel.AddAppelActivity
import com.delhomme.jobber.Appel.FragmentAppels
import com.delhomme.jobber.Candidature.AddCandidatureActivity
import com.delhomme.jobber.Candidature.FragmentCandidatures
import com.delhomme.jobber.Contact.AddContactActivity
import com.delhomme.jobber.Contact.FragmentContacts
import com.delhomme.jobber.Entreprise.FragmentEntreprises
import com.delhomme.jobber.Entretien.AddEntretienActivity
import com.delhomme.jobber.Entretien.FragmentEntretiens
import com.delhomme.jobber.Relance.FragmentRelances
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.Calendar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val REQUEST_NOTIFICATION_PERMISSION = 1001

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        // Bouton pour tester les notifications
        findViewById<Button>(R.id.btn_test_notification).setOnClickListener {
            triggerTestNotification()
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

    private fun triggerTestNotification() {
        val notificationIntent = Intent(this, NotificationReceiver::class.java)
        sendBroadcast(notificationIntent)
        Log.d("MainActivity", "triggerTestNotification : appuyer")
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
}
