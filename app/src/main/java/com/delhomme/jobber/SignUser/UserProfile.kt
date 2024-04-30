package com.delhomme.jobber.SignUser

data class UserProfile(
    val name: String,
    val birthDate: String,
    val email: String,
    val phone: String,
    val experiences : List<Experience> = listOf(),
    val skills : List<Skill> = listOf(),
    val educations: List<Education> = listOf()
)
