package com.delhomme.jobber.models

import java.util.Date

data class Entretiens(
    val id: Int,
    val candidature_id: Int,
    val date_entretien: Date,
)
