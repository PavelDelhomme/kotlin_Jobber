package com.delhomme.jobber.SignUser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.delhomme.jobber.R
import com.delhomme.jobber.ViewProfilsActivity
import com.google.gson.Gson

class AdditionalInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_additional_info)

        setupButtons()
    }

    private fun setupButtons() {
        val addExperienceButton = findViewById<Button>(R.id.addExperienceButton)
        val addSkillsButton = findViewById<Button>(R.id.addSkillButton)
        val addEducationButton = findViewById<Button>(R.id.addEducationButton)

        addExperienceButton.setOnClickListener { showFragment(ExperienceFragment::class.java) }
        addSkillsButton.setOnClickListener { showFragment(SkillFragment::class.java) }
        addEducationButton.setOnClickListener { showFragment(EducationFragment::class.java) }

        val submitButton = findViewById<Button>(R.id.submitAdditionalInfoButton)
        submitButton.setOnClickListener { saveAllAdditionalInfo() }
    }

    private fun <T: Fragment> showFragment(fragmentClass: Class<T>, args: Bundle? = null) {
        val fragment = fragmentClass.newInstance().apply {
            arguments = args
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
    fun addExperienceToList(experience: Experience) {
        addToList(R.id.experienceList, experience, formatExperienceToString)
    }

    fun addEducationToList(education: Education) {
        addToList(R.id.educationList, education, formatEducationToString)
    }

    fun addSkillToList(skill: Skill) {
        addToList(R.id.skillList, skill, formatSkillToString)
    }

    private fun <T> addToList(listId: Int, item: T, formatter: (T) -> String) {
        val list = findViewById<LinearLayout>(listId)
        val content = formatter(item)

        val cardView = CardView(this).apply {
            radius = 12f
            cardElevation = 6f
            setContentPadding(10, 10, 10, 10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(10, 10, 10, 10) }
        }

        val textView = TextView(this).apply { text = content }

        val buttonContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val editButton = Button(this).apply {
            text = "Modifier"
            setOnClickListener {
                if (item is Experience) {
                    showFragment(ExperienceFragment::class.java, Bundle().apply {
                        putString("experience", Gson().toJson(item))
                    })
                } else if (item is Education) {
                    showFragment(EducationFragment::class.java, Bundle().apply {
                        putString("education", Gson().toJson(item))
                    })
                } else if (item is Skill) {
                    showFragment(SkillFragment::class.java, Bundle().apply {
                        putString("skill", Gson().toJson(item))
                    })
                }
                list.removeView(cardView)
            }
        }

        val deleteButton = Button(this).apply {
            text = "X"
            setOnClickListener {
                list.removeView(cardView)
            }
        }

        buttonContainer.addView(editButton)
        buttonContainer.addView(deleteButton)

        cardView.addView(textView)
        cardView.addView(buttonContainer)
        list.addView(cardView)
    }


    private val formatExperienceToString: (Experience) -> String = { exp ->
        "${exp.titre} chez ${exp.entreprise} à ${exp.lieu}, ${exp.dateDebut} - ${exp.dateFin}"
    }

    private val formatEducationToString: (Education) -> String = { edu ->
        "${edu.niveau} - ${edu.intitule} à ${edu.etablissement}, ${edu.dateDebut} - ${edu.dateFin}, ${edu.localisation}"
    }

    private val formatSkillToString: (Skill) -> String = { skl ->
        "${skl.nom} - ${skl.categorie}"
    }

    private fun saveAllAdditionalInfo() {
        // Récupération des informations
        val experiences = extractList<Experience>(R.id.experienceList, formatExperienceFromCardView)
        val skills = extractList<Skill>(R.id.skillList, formatSkillFromCardView)
        val educations = extractList<Education>(R.id.educationList, formatEducationFromCardView)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("experiences", Gson().toJson(experiences))
        editor.putString("skills", Gson().toJson(skills))
        editor.putString("educations", Gson().toJson(educations))

        editor.apply()

        Toast.makeText(this, "Informations enregistrées avec succès !", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, ViewProfilsActivity::class.java))
    }

    private fun <T> extractList(listId: Int, extractor: (CardView) -> T): List<T> {
        val list = findViewById<LinearLayout>(listId)
        return (0 until list.childCount).mapNotNull { i ->
            val cardView = list.getChildAt(i) as? CardView
            cardView?.let { extractor(it) }
        }
    }
    private val formatExperienceFromCardView: (CardView) -> Experience = { card ->
        val content = (card.getChildAt(0) as TextView).text.toString()
        val parts = content.split(" chez ", " à ", ", ", " - ")
        Experience(
            titre = parts[0].trim(),
            entreprise = parts[1].trim(),
            lieu = parts[2].trim(),
            dateDebut = parts[3].trim(),
            dateFin = parts[4].trim(),
            competences = listOf(),
            missions = listOf()
        )
    }


    private val formatEducationFromCardView: (CardView) -> Education = { card ->
        val content = (card.getChildAt(0) as TextView).text.toString()
        val parts = content.split(" - ", " à ", " - ", " - ")
        if (parts.size >= 6) {
            Education(
                niveau = parts[0].trim(),
                intitule = parts[1].trim(),
                etablissement = parts[2].trim(),
                dateDebut = parts[3].trim(),
                dateFin = parts[4].trim(),
                localisation = parts[5].trim(),
                competences = listOf(),
            )
        } else {
            throw IllegalStateException("Incorrect format for education information")
        }
    }

    private val formatSkillFromCardView: (CardView) -> Skill = { card ->
        val content = (card.getChildAt(0) as TextView).text.toString()
        val parts = content.split(" - ")
        Skill(
            nom = parts[0].trim(),
            categorie = parts[1].trim()
        )
    }
}
