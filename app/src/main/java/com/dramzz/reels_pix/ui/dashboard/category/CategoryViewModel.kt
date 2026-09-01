package com.dramzz.reels_pix.ui.dashboard.category

import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.base.BaseViewModel
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.data.model.Movie
import com.dramzz.reels_pix.data.model.PaginatedSeriesResponse
import com.dramzz.reels_pix.data.model.toMovie
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val apiHelper: ApiHelper) : BaseViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isPaginating = MutableStateFlow(false)
    val isPaginating: StateFlow<Boolean> = _isPaginating

    private var currentPage = 1
    var hasMore = true
        private set

    private var currentMode: FetchMode? = null

    enum class FetchMode {
        GENRE, CATEGORY
    }

    private var currentParam: String = "" // can be genre string or category id string

    fun fetchByGenre(genre: String, isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
            hasMore = true
            _movies.value = emptyList()
        } else if (!hasMore || _isPaginating.value || isLoading.value) {
            return
        }

        currentMode = FetchMode.GENRE
        currentParam = genre

        fetchData("series/genre?type=$genre&page=$currentPage")
    }

    fun fetchByCategory(categoryId: Int, isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
            hasMore = true
            _movies.value = emptyList()
        } else if (!hasMore || _isPaginating.value || isLoading.value) {
            return
        }

        currentMode = FetchMode.CATEGORY
        currentParam = categoryId.toString()

        fetchData("series/category/$categoryId?page=$currentPage")
    }

    private fun fetchData(url: String) {
        if (currentPage == 1) {
            // initial load
            launchNetwork {
                executeFetch(url)
            }
        } else {
            // pagination
            viewModelScope.launch {
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
