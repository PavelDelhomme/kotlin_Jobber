package com.delhomme.jobber.models

data class Candidature(
    val id: String,
    val titreOffre: String,
    val entrepriseNom: String,
    val entrepriseInstance: Entreprise,
    val description: String,
    val date: String,
    val lieuDuPoste: String,
    val estSpontannee: Boolean,
    val dateSuppression: String?,
    val estCorbeille: Boolean,
    val notes: String?,
    val dateDuProchainEntretien: String?,
    val dateDuRetourEntretien: String?,
    val dateDerniereRelance: String?,
    val dateActuelleMoinsDateRetourEntretien: Int?,
    val etatCandidature: String,
    val typeEmploi: String,
    val plateformeUtilisee: String?,
    val fichiersSupplementaires: List<String>?,
    val technologiesUtilisees: List<String>?,
    val savoirsFaires: List<String>?,
    val savoirsEtres: List<String>?,
    val relances: List<Relance> = listOf(),
    val entretiens: List<Entretien> = listOf()
)
