package com.delhomme.jobber.SignUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.delhomme.jobber.R
import com.google.gson.Gson

class SkillFragment : Fragment() {
    private lateinit var nameField: EditText
    private lateinit var categoryField: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_skill, container, false)

        nameField = view.findViewById(R.id.name)
        categoryField = view.findViewById(R.id.category)
        saveButton = view.findViewById(R.id.saveSkill)
        cancelButton = view.findViewById(R.id.cancelSkill)

        val skillJson = arguments?.getString("skill")
        skillJson?.let {
            val skill = Gson().fromJson(it, Skill::class.java)
            nameField.setText(skill.nom)
            categoryField.setText(skill.categorie)
        }

        saveButton.setOnClickListener {
            val skill = Skill(
                nom = nameField.text.toString().trim(),
                categorie = categoryField.text.toString().trim()
            )

            (activity as AdditionalInfoActivity).addSkillToList(skill)

            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        return view
    }
}
