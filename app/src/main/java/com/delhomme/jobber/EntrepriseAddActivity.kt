package com.delhomme.jobber

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EntrepriseAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entreprise_add)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ajouter Entreprise"

        val cancelButton = findViewById<Button>(R.id.btnCancel)
        cancelButton.setOnClickListener {
            finish()
        }
    }
}
