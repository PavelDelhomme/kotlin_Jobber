package com.delhomme.jobber.Api.DjangoApi

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenService {
    @POST("token/refresh/")
    fun refreshToken(@Body params: Map<String, String>): Call<TokenResponse>
}


data class TokenResponse(val accessToken: String)