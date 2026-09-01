package com.dramzz.reels_pix.ui.dashboard.awards

import com.dramzz.reels_pix.base.BaseViewModel
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.data.model.RewardData
import com.dramzz.reels_pix.data.model.RewardResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class AwardsViewModel(
    private val apiHelper: ApiHelper
) : BaseViewModel() {

    private val _rewardData = MutableStateFlow<RewardData?>(null)
    val rewardData: StateFlow<RewardData?> = _rewardData.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchRewards() {
        launchNetwork {
            _error.value = null
            try {
                val response = apiHelper.callApi(Constants.REWARDS, Constants.ApiType.GET, useToken = true)
                if (response.isSuccessful) {
                    val rewardResponse = Gson().fromJson(response.body(), RewardResponse::class.java)
                    if (rewardResponse != null && rewardResponse.success) {
                        _rewardData.value = rewardResponse.data
                    } else {
                        _error.value = rewardResponse?.message ?: "Failed to fetch rewards"
                    }
                } else {
                    _error.value = "Failed to fetch rewards: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }

    fun claimReward(rewardId: Int, onResult: (Boolean, String) -> Unit) {
        launchNetwork {
            try {
                val response = apiHelper.callApi(
                    url = Constants.CLAIM_REWARD,
                    method = Constants.ApiType.POST,
                    params = mapOf("reward_id" to rewardId.toString()),
                    useToken = true,
                    isFormData = true
                )
                if (response.isSuccessful) {
                    val genericResponse = Gson().fromJson(response.body(), com.dramzz.reels_pix.data.model.GenericResponse::class.java)
                    if (genericResponse != null && genericResponse.success) {
                        onResult(true, genericResponse.message)
                        fetchRewards() // Refresh rewards data
                    } else {
                        onResult(false, genericResponse?.message ?: "Failed to claim reward")
                    }
                } else {
                    onResult(false, "Failed to claim reward: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.message}")
            }
        }
    }
}

