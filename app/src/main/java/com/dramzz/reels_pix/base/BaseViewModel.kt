package com.dramzz.reels_pix.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dramzz.reels_pix.base.network.NetworkError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

abstract class BaseViewModel : ViewModel() {

    private val _errorState = MutableStateFlow<NetworkError?>(null)
    val errorState: StateFlow<NetworkError?> = _errorState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        val networkError = when (exception) {
            is HttpException -> NetworkError.HttpError(exception.code(), exception.message())
            is SocketTimeoutException -> NetworkError.TimeoutError("Request timed out")
            is IOException -> NetworkError.NoConnectionError()
            else -> NetworkError.UnknownError(exception.message ?: "Unknown Error", exception)
        }
        _errorState.value = networkError
        _isLoading.value = false
    }

    protected fun launchNetwork(block: suspend () -> Unit) {
        _isLoading.value = true
        _errorState.value = null
        viewModelScope.launch(exceptionHandler) {
            block()
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorState.value = null
    }
}
