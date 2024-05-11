package com.delhomme.jobber


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.delhomme.jobber.AppelPacket.AppelAddActivity
import com.delhomme.jobber.CandidaturePacket.CandidatureAddActivity
import com.delhomme.jobber.ContactPacket.ContactAddActivity
import com.delhomme.jobber.ContactPacket.ContactListFragment
import com.delhomme.jobber.EntreprisePacket.EntrepriseAddActivity
import com.delhomme.jobber.EntretienPacket.EntretienAddActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var addContactLauncher: ActivityResultLauncher<Intent>
    private lateinit var addAppelLauncher: ActivityResultLauncher<Intent>
    private lateinit var addEntrepriseLauncher: ActivityResultLauncher<Intent>
    private lateinit var addEntretienLauncher: ActivityResultLauncher<Intent>
    private lateinit var addCandidatureLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                2 -> "Appels"
                3 -> "Contacts"
                4 -> "Entretiens"
                5 -> "Entreprises"
                else -> "Autres"
            }
        }.attach()

        // Initialize ActivityResultLauncher
        addContactLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("MainActivity", "Received result : $result")
            if (result.resultCode == Activity.RESULT_OK) {
                reloadFragment(3) // Recharger l'onglet Contacts (Index 3)
            }
        }
        // Initialize ActivityResultLauncher
        addAppelLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                reloadFragment(2) // Recharger l'onglet Contacts (Index 3)
            }
        }
        // Initialize ActivityResultLauncher
        addEntrepriseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                reloadFragment(5) // Recharger l'onglet Contacts (Index 3)
            }
        }
        // Initialize ActivityResultLauncher
        addEntretienLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                reloadFragment(4) // Recharger l'onglet Contacts (Index 3)
            }
        }
        // Initialize ActivityResultLauncher
        addCandidatureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                reloadFragment(1) // Recharger l'onglet Contacts (Index 3)
            }
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
                    val intent = Intent(this, CandidatureAddActivity::class.java)
                    addCandidatureLauncher.launch(intent)
                    true
                }
                R.id.menu_add_contact -> {
                    val intent = Intent(this, ContactAddActivity::class.java)
                    addContactLauncher.launch(intent)
                    true
                }
                R.id.menu_add_entretien -> {
                    val intent = Intent(this, EntretienAddActivity::class.java)
                    addEntretienLauncher.launch(intent)
                    true
                }
                R.id.menu_add_appel -> {
                    val intent = Intent(this, AppelAddActivity::class.java)
                    addAppelLauncher.launch(intent)
                    true
                }
                R.id.menu_add_entreprise -> {
                    val intent = Intent(this, EntrepriseAddActivity::class.java)
                    addEntrepriseLauncher.launch(intent)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun reloadFragment(index: Int) {
        // Cette méthode permet de rafraîchir le ViewPager.
        viewPager.setCurrentItem(index, false)
        (supportFragmentManager.findFragmentByTag("f$index") as? ContactListFragment)?.loadContacts()
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }
}
