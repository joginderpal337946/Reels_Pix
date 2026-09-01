package com.dramzz.reels_pix.ui.dashboard.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.base.local.SessionManager
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.data.model.RegisterData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GoogleUser(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String
)

class SignInViewModel(
    private val apiHelper: ApiHelper,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _signInSuccess = MutableStateFlow(false)
    val signInSuccess: StateFlow<Boolean> = _signInSuccess.asStateFlow()
    
    private val _signInError = MutableStateFlow<String?>(null)
    val signInError: StateFlow<String?> = _signInError.asStateFlow()

    fun performSocialLogin(
        googleUser: GoogleUser,
        deviceToken: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _signInError.value = null
            try {
                val params = mapOf(
                    "provider" to "google",
                    "provider_id" to googleUser.id,
                    "email" to googleUser.email,
                    "first_name" to googleUser.firstName,
                    "last_name" to googleUser.lastName,
                    "device_token" to deviceToken,
                    "device_type" to "android"
                )

                val response = apiHelper.callApi(
                    url = Constants.LOGIN,
                    method = Constants.ApiType.POST,
                    params = params,
                    isFormData = true,
                    useToken = false
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    val success = body?.get("success")?.asBoolean ?: false
                    if (success) {
                        val data = body?.getAsJsonObject("data")
                        val registerData = try {
                            com.google.gson.Gson().fromJson(data, RegisterData::class.java)
                        } catch (e: Exception) {
                            null
                        }

                        if (registerData != null && !registerData.accessToken.isNullOrEmpty()) {
                            sessionManager.setUserData(registerData)
                            sessionManager.setToken(registerData.accessToken)
                            _signInSuccess.value = true
                        } else {
                            _signInError.value = "Token not found in response."
                        }
                    } else {
                        _signInError.value = body?.get("message")?.asString ?: "Login failed"
                    }
                } else {
                    _signInError.value = "Server error: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Error during social login", e)
                _signInError.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
