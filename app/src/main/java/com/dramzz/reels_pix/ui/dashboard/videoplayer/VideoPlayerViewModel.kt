package com.dramzz.reels_pix.ui.dashboard.videoplayer

import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.base.BaseViewModel
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.data.model.FeedEpisode
import com.dramzz.reels_pix.data.model.PaginatedEpisodesResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.dramzz.reels_pix.base.local.SessionManager
import com.google.gson.JsonObject
import okhttp3.MultipartBody

class VideoPlayerViewModel(private val apiHelper: ApiHelper, private val sessionManager: SessionManager) : BaseViewModel() {

    private val _episodes = MutableStateFlow<List<FeedEpisode>>(emptyList())
    val episodes: StateFlow<List<FeedEpisode>> = _episodes

    private val _isPaginating = MutableStateFlow(false)
    val isPaginating: StateFlow<Boolean> = _isPaginating

    private var currentPage = 1
    var hasMore = true
        private set

    fun fetchEpisodes(seriesId: Int, isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
            hasMore = true
            _episodes.value = emptyList()
        } else if (!hasMore || _isPaginating.value || isLoading.value) {
            return
        }

        val url = "series/$seriesId/episodes?page=$currentPage"

        if (currentPage == 1) {
            launchNetwork {
                executeFetch(url)
            }
        } else {
            _isPaginating.value = true
            viewModelScope.launch {
                try {
                    executeFetch(url)
                } finally {
                    _isPaginating.value = false
                }
            }
        }
    }

    private suspend fun executeFetch(url: String) {
        val response = apiHelper.callApi(url, Constants.ApiType.GET)
        if (response.isSuccessful && response.body() != null) {
            val body = response.body()
            val paginatedResponse = Gson().fromJson(body, PaginatedEpisodesResponse::class.java)
            if (paginatedResponse != null && paginatedResponse.success) {
                val newEpisodes = paginatedResponse.data.episodesList
                _episodes.value = if (currentPage == 1) newEpisodes else _episodes.value + newEpisodes

                val meta = paginatedResponse.data.meta
                if (meta != null) {
                    hasMore = currentPage < meta.lastPage
                } else {
                    hasMore = false
                }

                if (hasMore) {
                    currentPage++
                }
            } else {
                // Ignore error mapping for now since BaseViewModel doesn't easily expose setError
            }
        }
    }

    fun toggleFavorite(episodeId: Int): Boolean {
        if (sessionManager.getToken().isNullOrEmpty()) {
            return false
        }

        // Optimistically update UI
        val currentEpisodes = _episodes.value.toMutableList()
        val index = currentEpisodes.indexOfFirst { it.id == episodeId }
        if (index != -1) {
            val ep = currentEpisodes[index]
            currentEpisodes[index] = ep.copy(isFavourite = !ep.isFavourite)
            _episodes.value = currentEpisodes
        }

        viewModelScope.launch {
            try {
                val formBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("episode_id", episodeId.toString())
                    .build()
                apiHelper.callApi("favorites/episodes/toggle", Constants.ApiType.POST, body = formBody, parts = formBody.parts)
            } catch (e: Exception) {
                // Revert on error
                val reverted = _episodes.value.toMutableList()
                val revIndex = reverted.indexOfFirst { it.id == episodeId }
                if (revIndex != -1) {
                    val ep = reverted[revIndex]
                    reverted[revIndex] = ep.copy(isFavourite = !ep.isFavourite)
                    _episodes.value = reverted
                }
            }
        }
        return true
    }

    fun toggleWishlist(seriesId: Int): Boolean {
        if (sessionManager.getToken().isNullOrEmpty()) {
            return false
        }

        // Optimistically update UI
        val currentEpisodes = _episodes.value.toMutableList()
        val index = currentEpisodes.indexOfFirst { it.seriesId == seriesId }
        val wasWishlisted = if (index != -1) currentEpisodes[index].isWishlistedSeries else false
        val newEpisodes = currentEpisodes.map { 
            if (it.seriesId == seriesId) it.copy(isWishlistedSeries = !wasWishlisted) else it 
        }
        _episodes.value = newEpisodes

        viewModelScope.launch {
            try {
                val formBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("series_id", seriesId.toString())
                    .build()
                apiHelper.callApi("saved/series/toggle", Constants.ApiType.POST, body = formBody, parts = formBody.parts)
            } catch (e: Exception) {
                // Revert on error
                val reverted = _episodes.value.map { 
                    if (it.seriesId == seriesId) it.copy(isWishlistedSeries = wasWishlisted) else it 
                }
                _episodes.value = reverted
            }
        }
        return true
    }

    private val maxWatchedEpisodes = mutableMapOf<Int, Int>()

    fun getMaxWatched(seriesId: Int): Int {
        return maxWatchedEpisodes[seriesId] ?: 0
    }

    /**
     * Records that the user watched [episodeNumber] of [seriesId] / [episodeId].
     * Only fired when the user is authenticated. Fire-and-forget — no UI state is mutated.
     */
    fun updateViewingHistory(seriesId: Int, episodeId: Int, episodeNumber: Int) {
        if (sessionManager.getToken().isNullOrEmpty()) return

        val currentMax = maxWatchedEpisodes[seriesId] ?: 0
        if (episodeNumber <= currentMax) {
            return
        }
        maxWatchedEpisodes[seriesId] = episodeNumber

        viewModelScope.launch {
            try {
                val formBody = okhttp3.MultipartBody.Builder()
                    .setType(okhttp3.MultipartBody.FORM)
                    .addFormDataPart("series_id", seriesId.toString())
                    .addFormDataPart("episode_id", episodeId.toString())
                    .addFormDataPart("episode_number", episodeNumber.toString())
                    .build()
                apiHelper.callApi(
                    url = com.dramzz.reels_pix.data.api.Constants.SERIES_HISTORY,
                    method = com.dramzz.reels_pix.data.api.Constants.ApiType.POST,
                    body = formBody,
                    parts = formBody.parts
                )
            } catch (_: Exception) {
                // Silently ignore — history update should never surface errors to the user
            }
        }
    }
}
