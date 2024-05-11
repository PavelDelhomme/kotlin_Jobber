package com.delhomme.jobber.models

import java.util.Date

data class Relance(
    val id: Int,
    val candidature_id: Int,
    val date_relance: Date
)
