package com.example.reels_pix.base.network

sealed class NetworkError : Exception() {
    object Network : NetworkError()
    object Timeout : NetworkError()
    data class Server(val code: Int, override val message: String?) : NetworkError()
    object Unknown : NetworkError()
}
