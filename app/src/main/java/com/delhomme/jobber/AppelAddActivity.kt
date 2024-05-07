package com.delhomme.jobber

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AppelAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appel_add)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ajouter Appel"

        val cancelButton = findViewById<Button>(R.id.btnCancel)
        cancelButton.setOnClickListener {
            finish()
        }
    }
}
