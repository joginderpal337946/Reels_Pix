package com.dramzz.reels_pix.ui.dashboard.mylist

import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.base.BaseViewModel
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.data.model.FeedSeries
import com.dramzz.reels_pix.data.model.PaginatedSeriesResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.dramzz.reels_pix.base.local.SessionManager
import okhttp3.MultipartBody

class SavedSeriesViewModel(
    private val apiHelper: ApiHelper, 
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _series = MutableStateFlow<List<FeedSeries>>(emptyList())
    val series: StateFlow<List<FeedSeries>> = _series

    private val _isPaginating = MutableStateFlow(false)
    val isPaginating: StateFlow<Boolean> = _isPaginating

    private var currentPage = 1
    var hasMore = true
        private set

    fun fetchSavedSeries(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
            hasMore = true
            _series.value = emptyList()
        } else if (!hasMore || _isPaginating.value || isLoading.value) {
            return
        }

        val url = "saved/series?page=$currentPage"

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
            val paginatedResponse = Gson().fromJson(body, PaginatedSeriesResponse::class.java)
            if (paginatedResponse != null && paginatedResponse.success) {
                val newSeries = paginatedResponse.data.seriesList
                _series.value = if (currentPage == 1) newSeries else _series.value + newSeries

                val meta = paginatedResponse.data.meta
                if (meta != null) {
                    hasMore = currentPage < meta.lastPage
                } else {
                    hasMore = false
                }

                if (hasMore) {
                    currentPage++
                }
            }
        }
    }

    fun toggleWishlist(seriesId: Int): Boolean {
        if (sessionManager.getToken().isNullOrEmpty()) {
            return false
        }

        val currentSeriesList = _series.value.toMutableList()
        val index = currentSeriesList.indexOfFirst { it.id == seriesId }
        
        if (index != -1) {
            val series = currentSeriesList[index]
            currentSeriesList[index] = series.copy(isWishlisted = !series.isWishlisted)
            _series.value = currentSeriesList
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
