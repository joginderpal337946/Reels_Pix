package com.dramzz.reels_pix.ui.dashboard.home

import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.base.BaseViewModel
import com.dramzz.reels_pix.base.local.SessionManager
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.data.model.HomeData
import com.dramzz.reels_pix.data.model.HomeResponse
import com.dramzz.reels_pix.data.model.PaginatedContinueWatchingResponse
import com.dramzz.reels_pix.data.model.ViewingHistoryItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val apiHelper: ApiHelper,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _homeData = MutableStateFlow<HomeData?>(null)
    val homeData: StateFlow<HomeData?> = _homeData

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _continueWatchingItem = MutableStateFlow<ViewingHistoryItem?>(null)
    val continueWatchingItem: StateFlow<ViewingHistoryItem?> = _continueWatchingItem

    fun fetchHomeData() {
        fetchContinueWatching()
        launchNetwork {
            _error.value = null
            try {
                val response = apiHelper.callApi(Constants.HOME, Constants.ApiType.GET)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    val homeResponse = Gson().fromJson(body, HomeResponse::class.java)
                    if (homeResponse != null && homeResponse.success) {
                        _homeData.value = homeResponse.data
                    } else {
                        _error.value = homeResponse?.message ?: "Failed to fetch data"
                    }
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred"
            }
        }
    }

    private fun fetchContinueWatching() {
        if (sessionManager.getToken().isNullOrEmpty()) return
        launchNetwork {
            try {
                val response = apiHelper.callApi("continue-watching", Constants.ApiType.GET)
                if (response.isSuccessful && response.body() != null) {
                    val cwResponse = Gson().fromJson(response.body(), PaginatedContinueWatchingResponse::class.java)
                    if (cwResponse?.success == true) {
                        _continueWatchingItem.value = cwResponse.data?.data?.firstOrNull()
                    }
                }
            } catch (e: Exception) {
                // Ignore errors for continue watching
            }
        }
    }

    fun updateContinueWatchingEpisode(seriesId: Int, episodeNumber: Int) {
        val currentItem = _continueWatchingItem.value
        if (currentItem != null && currentItem.series?.id == seriesId) {
            if (episodeNumber > currentItem.episodeNumber) {
                _continueWatchingItem.value = currentItem.copy(episodeNumber = episodeNumber)
            }
        }
    }
}
