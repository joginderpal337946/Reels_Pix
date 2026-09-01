package com.dramzz.reels_pix.ui.dashboard.profile

import androidx.lifecycle.ViewModel
import com.dramzz.reels_pix.base.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.dramzz.reels_pix.data.api.Constants
import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.data.api.ApiHelper
import kotlinx.coroutines.launch
import com.google.gson.Gson

class InvitationViewModel(
    private val sessionManager: SessionManager,
    private val apiHelper: ApiHelper
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        val token = sessionManager.getToken()
        _isLoggedIn.value = !token.isNullOrEmpty()
    }

    fun activateInvitationCode(code: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiHelper.callApi(
                    url = Constants.CLAIM_REWARD,
                    method = Constants.ApiType.POST,
                    params = mapOf("reward_id" to "9", "referral_code" to code),
                    useToken = true,
                    isFormData = true
                )
                if (response.isSuccessful) {
                    val genericResponse = Gson().fromJson(response.body(), com.dramzz.reels_pix.data.model.GenericResponse::class.java)
                    if (genericResponse != null && genericResponse.success) {
                        onResult(true, genericResponse.message)
                    } else {
                        onResult(false, genericResponse?.message ?: "Failed to activate invitation code")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val genericResponse = Gson().fromJson(errorBody, com.dramzz.reels_pix.data.model.GenericResponse::class.java)
                        genericResponse?.message ?: "Failed to activate invitation code: ${response.code()}"
                    } catch (e: Exception) {
                        "Failed to activate invitation code: ${response.code()}"
                    }
                    onResult(false, errorMessage)
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.message}")
            }
        }
    }
}
