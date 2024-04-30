package com.delhomme.jobber.SignUser

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.R
import com.google.gson.Gson

class SkillFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill_form)

        val nameField = findViewById<EditText>(R.id.name)
        val categoryField = findViewById<EditText>(R.id.category)
        val saveButton = findViewById<Button>(R.id.saveSkill)

        intent.getStringExtra("skill")?.let { skillJson ->
            val skill = Gson().fromJson(skillJson, Skill::class.java)
            nameField.setText(skill.nom)
            categoryField.setText(skill.categorie)
        }

        saveButton.setOnClickListener {
            val skill = Skill(
                categoryField.text.toString().trim(),
                nameField.text.toString().trim()
            )

            val intent = Intent()
            intent.putExtra("skill", Gson().toJson(skill))
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
