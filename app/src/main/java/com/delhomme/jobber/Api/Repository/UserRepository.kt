package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Api.DjangoApi.RetrofitClient
import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import com.delhomme.jobber.Api.LoginResponse
import com.delhomme.jobber.Api.UserProfileApi
import com.delhomme.jobber.Model.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(val context: Context) {
    private val userService = RetrofitClient.createService(UserProfileApi::class.java)
    init {
        LocalStorageManager.initialize(context)
    }

    fun loginUser(email: String, password: String, callback: Callback<LoginResponse>) {
        val loginInfo = mapOf("email" to email, "password" to password)
        userService.loginUser(loginInfo).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        LocalStorageManager.saveJWT(it.token)
                        callback.onResponse(call, response)
                    }
                } else {
                    callback.onFailure(call, RuntimeException("Failed to log in: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.onFailure(call, t)
            }
        })
    }

    fun registerUser(email: String, password: String, callback: Callback<LoginResponse>) {
        val userProfile = UserProfile(email = email, password = password)
        userService.createUserProfile(userProfile).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        LocalStorageManager.saveJWT(it.token)
                        callback.onResponse(call, response)
                    }
                } else {
                    callback.onFailure(call, RuntimeException("Failed to register: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.onFailure(call, t)
            }
        })
    }
}
