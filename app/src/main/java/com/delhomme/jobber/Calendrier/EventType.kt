package com.delhomme.jobber.Calendrier

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EventType : Parcelable {
    Candidature,
    Entretien,
    Relance,
    Appel,
}