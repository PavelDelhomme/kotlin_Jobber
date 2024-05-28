package com.delhomme.jobber.Api

import com.delhomme.jobber.Api.DjangoApi.ApiResponse
import com.delhomme.jobber.Model.UserProfile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

public interface UserProfileApi {
    @GET("users/profiles/{id}")
    fun getUserProfile(@Path("id") id: Int): Call<UserProfile>

    @POST("users/login/")
    fun loginUser(@Body loginInfo: Map<String, String>): Call<LoginResponse>

    @POST("users/register/")
    fun createUserProfile(@Body userProfile: UserProfile): Call<LoginResponse>

    @PUT("users/profiles/{id}")
    fun updateUserProfile(@Path("id") id: Int, @Body userProfile: UserProfile): Call<UserProfile>

    @DELETE("users/profiles/{id}")
    fun deleteUserProfile(@Path("id") id: Int): Call<Void>

    @POST("syncdata/")
    fun sendDataToServer(@Body jsonData: String): Call<ApiResponse>

}