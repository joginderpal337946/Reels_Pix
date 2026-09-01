package com.dramzz.reels_pix.ui.dashboard.profile

import com.dramzz.reels_pix.base.BaseViewModel

import com.dramzz.reels_pix.base.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.data.model.GenericResponse

class ProfileViewModel(
    private val sessionManager: SessionManager,
    private val apiHelper: ApiHelper
) : BaseViewModel() {
    private val _isLoggedIn = MutableStateFlow(!sessionManager.getToken().isNullOrEmpty())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow("Anonymous")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _coins = MutableStateFlow("0")
    val coins: StateFlow<String> = _coins.asStateFlow()

    private val _referralCode = MutableStateFlow("")
    val referralCode: StateFlow<String> = _referralCode.asStateFlow()

    init {
        checkLoginStatus()
        viewModelScope.launch {
            com.dramzz.reels_pix.utils.AuthEventBus.logoutEvent.collect {
                checkLoginStatus()
            }
        }
    }

    fun checkLoginStatus() {
        val loggedIn = !sessionManager.getToken().isNullOrEmpty()
        _isLoggedIn.value = loggedIn
        if (loggedIn) {
            val user = sessionManager.getUserData()
            val fullName = listOfNotNull(user?.firstName, user?.lastName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
            
            _userName.value = if (fullName.isNotBlank()) fullName else "Anonymous"
            _coins.value = user?.coins ?: "0"
            _referralCode.value = user?.referralCode ?: ""
        } else {
            _userName.value = "Anonymous"
            _coins.value = "0"
            _referralCode.value = ""
        }
    }

    fun signOut(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiHelper.callApi(Constants.LOGOUT, Constants.ApiType.POST, body = emptyMap<String, String>())
                if (response.isSuccessful) {
                    val genericResponse = com.google.gson.Gson().fromJson(response.body(), GenericResponse::class.java)
                    if (genericResponse != null && genericResponse.success) {
                        sessionManager.clear()
                        _isLoggedIn.value = false
                        _userName.value = "Anonymous"
                        _coins.value = "0"
                        _referralCode.value = ""
                        onComplete(true, genericResponse.message)
                    } else {
                        onComplete(false, genericResponse?.message ?: "Failed to log out")
                    }
                } else {
                    onComplete(false, "Failed to log out: ${response.code()}")
                }
            } catch (e: Exception) {
                onComplete(false, "Error: ${e.message}")
            }
        }
    }

    fun deleteAccount(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiHelper.callApi(Constants.PROFILE, Constants.ApiType.DELETE)
                if (response.isSuccessful) {
                    val genericResponse = com.google.gson.Gson().fromJson(response.body(), com.dramzz.reels_pix.data.model.GenericResponse::class.java)
                    if (genericResponse != null && genericResponse.success) {
                        sessionManager.clear()
                        _isLoggedIn.value = false
                        _userName.value = "Anonymous"
                        _coins.value = "0"
                        _referralCode.value = ""
                        onComplete(true, genericResponse.message)
                    } else {
                        onComplete(false, genericResponse?.message ?: "Failed to delete account")
                    }
                } else {
                    onComplete(false, "Failed to delete account: ${response.code()}")
                }
            } catch (e: Exception) {
                onComplete(false, "Error: ${e.message}")
            }
        }
    }
}

