package com.delhomme.jobber.Api.DjangoApi

import com.delhomme.jobber.Api.UserProfileApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://127.0.0.1:8000/api/"

    val instance: UserProfileApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(UserProfileApi::class.java)
    }
}