package com.dramzz.reels_pix.base.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityProvider {
    val isConnected: Flow<Boolean>
    fun hasNetworkConnection(): Boolean
}

