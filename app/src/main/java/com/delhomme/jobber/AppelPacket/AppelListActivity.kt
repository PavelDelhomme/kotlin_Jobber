package com.delhomme.jobber.AppelPacket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R

class AppelListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appel_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val appelRecyclerView = findViewById<RecyclerView>(R.id.recyclerAppels)
        appelRecyclerView.layoutManager = LinearLayoutManager(this)

        val appels = listOf<Appel>()

        val adapter = AppelAdapter(appels)
        appelRecyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }

}