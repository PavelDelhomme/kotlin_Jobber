package com.delhomme.jobber.Relance.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Relance(
    val id: String = UUID.randomUUID().toString(),
    val date_relance: Date,
    val plateformeUtilisee: String,
    val entrepriseNom: String,
    val contactId: String?,
    val candidatureId: String,
    val notes: String?,
) : Parcelable