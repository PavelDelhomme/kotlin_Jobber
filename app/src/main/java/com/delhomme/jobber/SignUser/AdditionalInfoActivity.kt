package com.delhomme.jobber.SignUser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.delhomme.jobber.R
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

        addExperienceButton.setOnClickListener { showExperienceFragment(null) }
        addSkillsButton.setOnClickListener { showSkillFragment(null) }
        addEducationButton.setOnClickListener { showEducationFragment(null) }

        val submitButton = findViewById<Button>(R.id.submitAdditionalInfoButton)
        submitButton.setOnClickListener { saveAllAdditionalInfo() }

    }


    private fun <T : Fragment> showFragment(fragmentClass: Class<T>, args: Bundle? = null) {
        val fragment = fragmentClass.newInstance().apply {
            arguments = args
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun addExperienceToList(experience: Experience) {
        addToList(R.id.experienceList, experience, formatExperience)
    }

    /*
    fun addExperienceToList(experience: Experience) {
        val experienceList = findViewById<LinearLayout>(R.id.experienceList)

        val cardView = CardView(this).apply {
            radius = 12f
            cardElevation = 6f
            setContentPadding(10, 10, 10, 10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10, 10, 10, 10)
            }
        }

        val textView = TextView(this).apply {
            text = "${experience.titre} chez ${experience.entreprise} à ${experience.lieu}, ${experience.dateDebut} - ${experience.dateFin}"
        }

        cardView.addView(textView)
        experienceList.addView(cardView)
    }*/
    fun addEducationToList(education: Education) {
        addToList(R.id.educationList, education, formatEducation)
    }

    /*
    fun addEducationToList(education: Education) {
        val educationList = findViewById<LinearLayout>(R.id.educationList)

        val cardView = CardView(this).apply {
            radius = 12f
            cardElevation = 6f
            setContentPadding(10, 10, 10, 10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10, 10, 10, 10)
            }
        }

        val textView = TextView(this).apply {
            text = "${education.niveau} - ${education.intitule} à ${education.etablissement}, ${education.dateDebut} - ${education.dateFin}"
        }

        cardView.addView(textView)
        educationList.addView(cardView)
    }*/
    fun addSkillToList(skill: Skill) {
        addToList(R.id.skillList, skill, formatSkill)
    }
    /*
    fun addSkillToList(skill: Skill) {
        val skillList = findViewById<LinearLayout>(R.id.skillList)

        val cardView = CardView(this).apply {
            radius = 12f
            cardElevation = 6f
            setContentPadding(10, 10, 10, 10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10, 10, 10, 10)
            }
        }

        val textView = TextView(this).apply {
            text = "${skill.nom} - ${skill.categorie}"
        }

        cardView.addView(textView)
        skillList.addView(cardView)
    }*/

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

        cardView.addView(textView)
        list.addView(cardView)
    }


    private val formatExperience: (CardView) -> Experience = { card ->
        val content = (card.getChildAt(0) as TextView).text.toString()
        val parts = content.split(" chez ", " à ", ", ", " - ")
        if (parts.size >= 5) {
            Experience(
                titre = parts[0].trim(),
                entreprise = parts[1].trim(),
                lieu = parts[2].trim(),
                dateDebut = parts[3].trim(),
                dateFin = parts[4].trim(),
                competences = listOf(),
                missions = listOf()
            )
        } else null
    }
    private val formatEducation: (CardView) -> Education = { card ->
        val content = (card.getChildAt(0) as TextView).text.toString()
        val parts = content.split(" - ", " à ", ", ", " - ")
        if (parts.size >= 4) {
            Education(
                niveau = parts[0].trim(),
                intitule = parts[1].trim(),
                etablissement = parts[2].trim(),
                dateDebut = parts[3].trim(),
                dateFin = parts[4].trim(),
                competences = listOf()
            )
        } else null
    }

    private val formatSkill: (CardView) -> Skill = { card ->
        val content = (card.getChildAt(0) as TextView).text.toString()
        val parts = content.split(" - ")
        if (parts.size >= 2) {
            Skill(
                nom = parts[0].trim(),
                categorie = parts[1].trim()
            )
        } else null
    }

    private fun showExperienceFragment(experience: Experience?) {
        val fragment = ExperienceFragment()
        experience?.let {
            val args = Bundle()
            args.putString("experience", Gson().toJson(experience))
            fragment.arguments = args
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.experienceContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showEducationFragment(education: Education?) {
        val fragment = EducationFragment()
        education?.let {
            val args = Bundle()
            args.putString("education", Gson().toJson(education))
            fragment.arguments = args
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.experienceContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showSkillFragment(skill: Skill?) {
        val fragment = SkillFragment()
        skill?.let {
            val args = Bundle()
            args.putString("skill", Gson().toJson(skill))
            fragment.arguments = args
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.experienceContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun <T> extractList(listId: Int, extractor: (CardView) -> T): List<T> {
        val list = findViewById<LinearLayout>(listId)
        return (0 until list.childCount).mapNotNull { i ->
            val cardView = list.getChildAt(i) as? CardView
            cardView?.let { extractor(it) }
        }
    }

    private fun saveAllAdditionalInfo() {

    }
}