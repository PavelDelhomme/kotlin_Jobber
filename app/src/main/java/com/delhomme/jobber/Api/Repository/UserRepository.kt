package com.delhomme.jobber.Api.Repository

import android.content.Context
import com.delhomme.jobber.Model.User

class UserRepository(val context: Context) {
    fun getUserData(): User? {
        return User("john@doe.com", "123456789")
    }

    fun saveUserData(user: User) {

    }
}