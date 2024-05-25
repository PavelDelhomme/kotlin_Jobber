package com.delhomme.jobber.Entreprise.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Entreprise(
    val nom: String,
    var contactIds: MutableList<String> = mutableListOf(),
    var relanceIds: MutableList<String> = mutableListOf(),
    var entretiens: MutableList<String> = mutableListOf(),
    var candidatureIds: MutableList<String> = mutableListOf(),
    var eventIds: MutableList<String> = mutableListOf()
) : Parcelable {
    override fun toString(): String = nom
}
