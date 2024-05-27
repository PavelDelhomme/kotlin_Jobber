package com.delhomme.jobber.Api

import com.delhomme.jobber.Model.UserProfile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserProfileApi {
    @GET("user-profiles/{id}")
    fun getUserProfile(@Path("id") id: Int): Call<UserProfile>

    @POST("user-profiles")
    fun createUserProfile(@Body userProfile: UserProfile): Call<UserProfile>

    @PUT("user-profiles/{id}")
    fun updateUserProfile(@Path("id") id: Int, @Body userProfile: UserProfile): Call<UserProfile>

    @DELETE("user-profiles/{id}")
    fun deleteUserProfile(@Path("id") id: Int): Call<Void>
}