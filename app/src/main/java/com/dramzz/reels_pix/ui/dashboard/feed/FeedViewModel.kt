package com.dramzz.reels_pix.ui.dashboard.feed

import com.dramzz.reels_pix.base.BaseViewModel
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.data.model.FeedResponse
import com.dramzz.reels_pix.data.model.FeedSeries
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import com.dramzz.reels_pix.base.local.SessionManager
import okhttp3.MultipartBody
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class FeedViewModel(
    private val apiHelper: ApiHelper,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _feedSeriesList = MutableStateFlow<List<FeedSeries>>(emptyList())
    val feedSeriesList: StateFlow<List<FeedSeries>> = _feedSeriesList.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchFeed()
    }

    fun fetchFeed() {
        launchNetwork {
            _error.value = null
            try {
                val response = apiHelper.callApi(Constants.FEED, Constants.ApiType.GET, useToken = false)
                if (response.isSuccessful) {
                    val feedResponse = Gson().fromJson(response.body(), FeedResponse::class.java)
                    if (feedResponse != null && feedResponse.success) {
                        _feedSeriesList.value = feedResponse.data ?: emptyList()
                    } else {
                        _error.value = feedResponse?.message ?: "Failed to fetch feed"
                    }
                } else {
                    _error.value = "Failed to fetch feed: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }

    fun toggleFavorite(episodeId: Int): Boolean {
        if (sessionManager.getToken().isNullOrEmpty()) {
            return false
        }

        // Optimistically update
        val currentFeed = _feedSeriesList.value.toMutableList()
        var targetSeriesIndex = -1
        var targetEpisodeIndex = -1
        for ((sIndex, series) in currentFeed.withIndex()) {
            val eIndex = series.episodes?.indexOfFirst { it.id == episodeId } ?: -1
            if (eIndex != -1) {
                targetSeriesIndex = sIndex
                targetEpisodeIndex = eIndex
                break
            }
        }

        if (targetSeriesIndex != -1) {
            val series = currentFeed[targetSeriesIndex]
            val updatedEpisodes = series.episodes?.map { ep ->
                if (ep.id == episodeId) ep.copy(isFavourite = !ep.isFavourite) else ep
            } ?: emptyList()
            currentFeed[targetSeriesIndex] = series.copy(episodes = updatedEpisodes)
            _feedSeriesList.value = currentFeed
        }

        viewModelScope.launch {
            try {
                val formBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("episode_id", episodeId.toString())
                    .build()
                apiHelper.callApi("favorites/episodes/toggle", Constants.ApiType.POST, body = formBody, parts = formBody.parts)
            } catch (e: Exception) {
                // Ignore for now, optimistic UI is assumed successful
            }
        }
        return true
    }

    fun toggleWishlist(seriesId: Int): Boolean {
        if (sessionManager.getToken().isNullOrEmpty()) {
            return false
        }

        // Optimistically update UI
        val currentFeed = _feedSeriesList.value.toMutableList()
        val index = currentFeed.indexOfFirst { it.id == seriesId }
        if (index != -1) {
            val series = currentFeed[index]
            currentFeed[index] = series.copy(isWishlisted = !series.isWishlisted)
            _feedSeriesList.value = currentFeed
        }

        viewModelScope.launch {
            try {
                val formBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("series_id", seriesId.toString())
                    .build()
                apiHelper.callApi("saved/series/toggle", Constants.ApiType.POST, body = formBody, parts = formBody.parts)
            } catch (e: Exception) {
                // Ignore for now
            }
        }
        return true
    }
}
