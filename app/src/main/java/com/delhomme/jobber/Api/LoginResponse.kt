package com.delhomme.jobber.Api

import com.delhomme.jobber.Model.UserProfile

data class LoginResponse(
    val userProfile: UserProfile,
    val token: String
)
