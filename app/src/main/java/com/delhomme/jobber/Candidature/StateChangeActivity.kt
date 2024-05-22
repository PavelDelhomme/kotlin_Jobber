package com.delhomme.jobber.Candidature

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.R


class StateChangeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_state_change)

        val spinnerState = findViewById<Spinner>(R.id.spinnerState)
        ArrayAdapter.createFromResource(
            this, R.array.candidature_states, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerState.adapter = adapter
        }

        val btnSave = Button(this).apply {
            setText("Save")
            setOnClickListener {
                val selectedState = spinnerState.selectedItem.toString()
                val intent = intent
                intent.putExtra("selectedState", selectedState)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        val btnCancel = Button(this).apply {
            setText("Cancel")
            setOnClickListener { finish() }
        }
    }
}