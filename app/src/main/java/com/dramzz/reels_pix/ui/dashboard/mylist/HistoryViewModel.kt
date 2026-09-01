package com.dramzz.reels_pix.ui.dashboard.mylist

import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.base.BaseViewModel
import com.dramzz.reels_pix.base.local.SessionManager
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.data.model.ViewingHistoryItem
import com.dramzz.reels_pix.data.model.ViewingHistoryResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val apiHelper: ApiHelper,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _historyItems = MutableStateFlow<List<ViewingHistoryItem>>(emptyList())
    val historyItems: StateFlow<List<ViewingHistoryItem>> = _historyItems

    init {
        fetchHistory()
    }

    fun fetchHistory() {
        if (sessionManager.getToken().isNullOrEmpty()) return
        
        launchNetwork {
            val response = apiHelper.callApi(Constants.SERIES_HISTORY)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val genericResp = Gson().fromJson(body, ViewingHistoryResponse::class.java)
                    if (genericResp.success && genericResp.data != null) {
                        _historyItems.value = genericResp.data
                    }
                }
            }
        }
    }

    fun toggleWishlist(seriesId: Int): Boolean {
        if (sessionManager.getToken().isNullOrEmpty()) {
            return false
        }

        val currentList = _historyItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.series?.id == seriesId }
        
        if (index != -1) {
            val item = currentList[index]
            if (item.series != null) {
                val updatedSeries = item.series.copy(isWishlisted = !item.series.isWishlisted)
                currentList[index] = item.copy(series = updatedSeries)
                _historyItems.value = currentList
            }
        }

        viewModelScope.launch {
            try {
                val formBody = okhttp3.MultipartBody.Builder()
                    .setType(okhttp3.MultipartBody.FORM)
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
