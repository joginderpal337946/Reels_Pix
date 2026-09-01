package com.dramzz.reels_pix.base.network

sealed class NetworkError(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    class HttpError(val code: Int, message: String) : NetworkError(message)
    class TimeoutError(message: String) : NetworkError(message)
    class UnknownError(message: String, cause: Throwable? = null) : NetworkError(message, cause)
    class NoConnectionError(message: String = "No internet connection") : NetworkError(message)
}
