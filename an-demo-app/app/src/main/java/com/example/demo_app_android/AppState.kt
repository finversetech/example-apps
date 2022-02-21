package com.example.demo_app_android

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class UserData(val name: String, val value: String?)

class UserStateViewModel : ViewModel() {
    var isLoggedIn by mutableStateOf(false)
    var isBusy by mutableStateOf(false)
    var accessToken by mutableStateOf("")
    var userData: ArrayList<UserData> by mutableStateOf(ArrayList())

    fun signIn(username: String, password: String) {
        isBusy = true
        login(LoginCredential(username, password)) {
            if (it != null) {
                accessToken = it
                isLoggedIn = true
            }
            isBusy = false
        }
    }

    fun fetchUser() {
        isBusy = true
        fetchUser(accessToken) {
            if (it != null) {
                val institution = it.institution
                val liid = it.liid
                if (institution != null) {
                    userData.add(UserData("institution", institution.name))
                }
                if (liid != null) {
                    userData.add(UserData("liid", liid.liid))
                }
            }
            isBusy = false
        }
    }
}

val UserState = compositionLocalOf<UserStateViewModel> { error("User State Context Not Found!") }
