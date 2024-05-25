package com.delhomme.jobber.Contact.model

import android.os.Parcelable
import com.delhomme.jobber.Entreprise.model.Entreprise
import kotlinx.parcelize.Parcelize
import java.util.UUID


@Parcelize
data class Contact(
    val id : String = UUID.randomUUID().toString(),
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val entrepriseNom: String,
    val appelsIds: MutableList<String> = mutableListOf(),
    val candidatureIds: MutableList<String>? = mutableListOf(),
) : Parcelable {
    fun getFullName(): String = "$prenom $nom"
    fun getFullNameEntreprise(entreprises: List<Entreprise>): String {
        val entrepriseNom = entreprises.find { it.nom == this.entrepriseNom }?.nom ?: "Entreprise inconnue"
        return "$prenom $nom $entrepriseNom"
    }
}
