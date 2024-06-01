package com.delhomme.jobber.Api.DjangoApi

import android.util.Log
import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import com.delhomme.jobber.Api.UserProfileApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //private const val BASE_URL_0 = "http://192.168.1.133:8000/api/"
    //private const val BASE_URL_0 = "http://192.168.1.133:8000/"
    private const val BASE_URL_0 = "http://10.0.2.2:8000/"
    //private const val BASE_URL_3 = "http://192.168.1.133:8000/api/"
    //private const val BASE_URL_4 = "https://jobs.delhomme.ovh/api/"

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
            Log.d("RetrofitClient", "RetrofitClient: response send: $response")

            // Verification si token est expiré
            if(response.code() == 401) {
                val refreshToken = LocalStorageManager.getRefreshToken()
                Log.d("RetrofitClient", "RetrofitClient: refreshToken : $refreshToken")
                if (refreshToken != null) {
                    Log.d("RetrofitClient", "RetrofitClient: refreshToken is not null")
                    val newAccessToken = refreshToken(refreshToken)
                    Log.d("RetrofitClient", "RetrofitClient: newAccesToken: $newAccessToken")
                    if (newAccessToken.isNotEmpty()) {
                        Log.d("RetrofitClient", "RetrofitClient: newAccessToken is not Empty")
                        LocalStorageManager.saveJWT(newAccessToken)
                        Log.d("RetrofitClient", "RetrofitClient: newAccessTokenSaved into LocalStorageManager")
                        request = request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                        Log.d("RetrofitClient", "RetrofitClient : request : $request")
                        Log.d("RetrofitClient", "RetrofitClient : request headers : ${request.headers()}")
                        return@addInterceptor chain.proceed(request)
                    }
                }
            }
            response
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_0)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createService(serviceClass: Class<T>): T {
        Log.d("RetrofitClient", "createService called")
        return retrofit.create(serviceClass)
    }

    private fun refreshToken(refreshToken: String): String {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_0)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //val tokenService = retrofit.create(TokenService::class.java)
        val tokenService = retrofit.create(UserProfileApi::class.java)
        Log.d("RetrofitClient", "RetrofitClient: refreshToken : tokenService : $tokenService")
        val params = mapOf("refresh" to refreshToken)
        Log.d("RetrofitClient", "RetrofitClient: refreshToken : params : $params")
        val response = tokenService.refreshToken(params).execute()
        Log.d("RetrofitClient", "RetrofitClient: response : $response")
        return if (response.isSuccessful) {
            Log.d("RetrofitClient", "RetrofitClient: refreshToken: response.isSucessful ${response.body()?.accessToken ?: ""}")
            response.body()?.accessToken ?: ""
        } else {
            Log.d("RetrofitClient", "RetrofitClient: refreshToken: response no successfyl return blank string")
            ""
        }
    }
}