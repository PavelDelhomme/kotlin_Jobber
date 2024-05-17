package com.delhomme.jobber

enum class CandidatureState(val priority: Int) {
    CANDIDATEE_ET_EN_ATTENTE(1),
    EN_ATTENTE_APRES_ENTRETIEN(2),
    FAIRE_UN_RETOUR_POST_ENTRETIEN(3),
    A_RELANCEE_APRES_ENTRETIEN(4),
    A_RELANCEE(5),
    RELANCEE_ET_EN_ATTENTE(6),
    AUCUNE_REPONSE(7),
    NON_RETENU(8),
    ERREUR(9)
}