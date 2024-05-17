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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.delhomme.jobber.Appel.AddAppelActivity
import com.delhomme.jobber.Candidature.AddCandidatureActivity
import com.delhomme.jobber.Contact.AddContactActivity
import com.delhomme.jobber.Entretien.AddEntretienActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val REQUEST_NOTIFICATION_PERMISSION = 1001

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
        } else {
            triggerTestNotification()
            configureNotificationAlarm()
        }

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        // Configuration du ViewPager avec un adapter personnalisé
        val viewPagerAdapter = MainViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        // Relier le TabLayout au ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Dashboard"
                1 -> "Candidatures"
                2 -> "Contacts"
                3 -> "Appels"
                4 -> "Entreprises"
                5 -> "Entretiens"
                6 -> "Relances"
                else -> "Autres"
            }
        }.attach()

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
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

    private fun reloadFragment(index: Int) {
        // Cette méthode permet de rafraîchir le ViewPager.
        viewPager.setCurrentItem(index, false)
        // (supportFragmentManager.findFragmentByTag("f$index") as? ContactListFragment)?.loadContacts()
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }
}
