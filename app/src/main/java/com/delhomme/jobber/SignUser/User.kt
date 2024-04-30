package com.delhomme.jobber.SignUser

data class User(
    val email: String,
    val password: String,
    val profile: UserProfile,
    val experiences: List<Experience> = listOf(),
    val skills: List<Skill> = listOf(),
    val educations: List<Education> = listOf()
)