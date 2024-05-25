package com.delhomme.jobber.Appel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Appel(
    val id: String = UUID.randomUUID().toString(),
    val candidature_id: String? = null,
    val contact_id: String? = null,
    val entrepriseNom: String?,
    var date_appel: Date,
    var objet: String,
    var archivee: Boolean = false,
    var notes: String,
) : Parcelable
