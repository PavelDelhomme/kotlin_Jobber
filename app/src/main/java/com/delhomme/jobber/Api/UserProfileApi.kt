package com.delhomme.jobber.Api

import com.delhomme.jobber.Api.DjangoApi.TokenResponse
import com.delhomme.jobber.Model.UserProfile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserProfileApi {
    @POST("token/")
    fun loginUser(@Body loginInfo: Map<String, String>): Call<LoginResponse>

    @POST("token/refresh/")
    fun refreshToken(@Body refreshInfo: Map<String, String>): Call<TokenResponse>

    @POST("users/register/")
    fun createUserProfile(@Body userProfile: UserProfile): Call<LoginResponse>
}