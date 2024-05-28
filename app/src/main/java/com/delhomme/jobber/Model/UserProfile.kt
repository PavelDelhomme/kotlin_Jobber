package com.delhomme.jobber.Model

data class UserProfile(
    val email: String,
    val password: String,
    val telephone: String? = null,
    val birthDate: String? = null
)
