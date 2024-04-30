package com.delhomme.jobber.SignUser


data class Experience(
    val titre: String,
    val entreprise: String,
    val dateDebut: String,
    val dateFin: String,
    val lieu: String,
    val missions: List<String>,
    val competences: List<Skill>
)

data class Skill(
    val categorie: String,
    val nom: String,
    val associeAvecExperience: Boolean = false,
    val associeAvecFormation: Boolean = false
)

data class Education(
    val niveau: String,
    val intitule: String,
    val etablissement: String,
    val dateDebut: String,
    val dateFin: String,
    val localisation: String,
    val competences: List<Skill>
)