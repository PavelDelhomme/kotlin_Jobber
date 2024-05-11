
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.AppelPacket.AppelAddActivity
import com.delhomme.jobber.CandidaturePacket.Candidature
import com.delhomme.jobber.CandidaturePacket.CandidatureAdapter
import com.delhomme.jobber.CandidaturePacket.CandidatureAddActivity
import com.delhomme.jobber.CandidaturePacket.CandidatureDetailsActivity
import com.delhomme.jobber.ContactPacket.ContactAddActivity
import com.delhomme.jobber.EntreprisePacket.EntrepriseAddActivity
import com.delhomme.jobber.EntretienPacket.EntretienAddActivity
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson


class CandidatureListActivity : AppCompatActivity() {
    private val candidatures = mutableListOf<Candidature>()
    private lateinit var candidatureAdapter: CandidatureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidature_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Candidatures"

        val candidatureRecyclerView = findViewById<RecyclerView>(R.id.recyclerCandidatures)
        candidatureRecyclerView.layoutManager = LinearLayoutManager(this)

        candidatureAdapter = CandidatureAdapter(candidatures) { candidature ->
            showCandidatureDetails(candidature)
        }
        candidatureRecyclerView.adapter = candidatureAdapter

        loadCandidatures()

        val fabAddCandidature = findViewById<FloatingActionButton>(R.id.fabAddCandidature)
        fabAddCandidature.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showCandidatureDetails(candidature: Candidature) {
        val intent = Intent(this, CandidatureDetailsActivity::class.java)
        intent.putExtra("candidature_id", candidature.id)
        startActivity(intent)
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_add_items, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_add_candidature -> {
                    startActivity(Intent(this, CandidatureAddActivity::class.java))
                    true
                }
                R.id.menu_add_contact -> {
                    startActivity(Intent(this, ContactAddActivity::class.java))
                    true
                }
                R.id.menu_add_entretien -> {
                    startActivity(Intent(this, EntretienAddActivity::class.java))
                    true
                }
                R.id.menu_add_appel -> {
                    startActivity(Intent(this, AppelAddActivity::class.java))
                    true
                }
                R.id.menu_add_entreprise -> {
                    startActivity(Intent(this, EntrepriseAddActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }

    private fun loadCandidatures() {
        Log.d("CandidatureListActivity", "loadCandidatures called")
        val sharedPreferences = getSharedPreferences("candidatures_prefs", MODE_PRIVATE)
        val gson = Gson()

        candidatures.clear()
        for ((key, value) in sharedPreferences.all) {
            if (key.startsWith("candidature_")) {
                val candidatureJson = value as String
                val candidature = gson.fromJson(candidatureJson, Candidature::class.java)
                candidatures.add(candidature)

                Log.d("Activity Liste des Candidatures ", "candidature : $candidature")
                Log.d(
                    "Activity Liste candidatures ",
                    "candidature.offre : ${candidature.titreOffre}"
                )
            }
        }
        candidatures.sortByDescending { it.date }
        candidatureAdapter.notifyDataSetChanged()
    }
}
