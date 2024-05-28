package com.delhomme.jobber.Api.DjangoApi

import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.133:8000/api/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            var request = chain.request()
            val accessToken = LocalStorageManager.getJWT()
            if(accessToken != null) {
                request = request.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            }
            val response = chain.proceed(request)

            // Verification si token est expiré
            if(response.code() == 401) {
                val refreshToken = LocalStorageManager.getRefreshToken()
                if (refreshToken != null) {
                    val newAccessToken = refreshToken(refreshToken)
                    LocalStorageManager.saveJWT(newAccessToken)
                    request = request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                    return@addInterceptor chain.proceed(request)
                }
            }
            response
        }
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    private fun refreshToken(refreshToken: String): String {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val tokenService = retrofit.create(TokenService::class.java)
        val params = mapOf("refresh" to refreshToken)
        val response = tokenService.refreshToken(params).execute()
        return if (response.isSuccessful) {
            response.body()?.accessToken ?: ""
        } else {
            ""
        }
    }
}