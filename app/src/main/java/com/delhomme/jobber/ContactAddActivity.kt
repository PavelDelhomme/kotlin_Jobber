package com.delhomme.jobber

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ContactAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_add)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ajouter Contact"

        val cancelButton = findViewById<Button>(R.id.btnCancel)
    }
}