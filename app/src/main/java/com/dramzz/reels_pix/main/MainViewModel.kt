package com.dramzz.reels_pix.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.base.local.SessionManager
import com.dramzz.reels_pix.utils.ConnectivityObserver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val sessionManager: SessionManager,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val isNetworkAvailable = connectivityObserver.observe()
        .map { it == ConnectivityObserver.Status.Available }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
}