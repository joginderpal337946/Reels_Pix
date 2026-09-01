package com.dramzz.reels_pix.ui.dashboard.search

import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.base.BaseViewModel
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.data.model.Movie
import com.dramzz.reels_pix.data.model.PaginatedSeriesResponse
import com.dramzz.reels_pix.data.model.toMovie
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val apiHelper: ApiHelper) : BaseViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isPaginating = MutableStateFlow(false)
    val isPaginating: StateFlow<Boolean> = _isPaginating

    private var currentPage = 1
    var hasMore = true
        private set

    private var currentQuery: String = ""
    private var searchJob: Job? = null

    fun search(query: String, isRefresh: Boolean = false) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (isRefresh || query != currentQuery) {
                currentPage = 1
                hasMore = true
                _movies.value = emptyList()
                currentQuery = query
                // Add a small debounce delay for fresh typing searches
                if (query.isNotEmpty()) delay(500)
            } else if (!hasMore || _isPaginating.value || isLoading.value) {
                return@launch
            }

            val url = "series?search=${currentQuery}&page=$currentPage"
            
            if (currentPage == 1) {
                launchNetwork {
                    executeFetch(url)
                }
            } else {
                _isPaginating.value = true
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
                val newMovies = paginatedResponse.data.seriesList.map { it.toMovie() }
                _movies.value = if (currentPage == 1) newMovies else _movies.value + newMovies
                
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
}
