package com.delhomme.jobber.Utils

enum class CandidatureState(val priority: Int) {
    CANDIDATEE_ET_EN_ATTENTE(1),
    EN_ATTENTE_APRES_ENTRETIEN(2),
    EN_ATTENTE_D_UN_ENTRETIEN(3),
    FAIRE_UN_RETOUR_POST_ENTRETIEN(4),
    A_RELANCEE_APRES_ENTRETIEN(5),
    A_RELANCEE(6),
    RELANCEE_ET_EN_ATTENTE(7),
    AUCUNE_REPONSE(8),
    NON_RETENU(9),
    NON_RETENU_APRES_ENTRETIEN(10),
    NON_RETENU_SANS_ENTRETIEN(11),
    ACCEPTEE(12),  // Nouvel état pour une réponse positive
    ERREUR(13)
}
