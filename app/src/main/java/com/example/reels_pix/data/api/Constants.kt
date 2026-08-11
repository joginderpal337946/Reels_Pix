package com.example.reels_pix.data.api

object Constants {
    const val BASE_URL = "https://api.example.com/"
    const val TIMEOUT = 30L

    // Dummy API Endpoints
    const val LOGIN = "api/v1/auth/login"
    const val GET_PROFILE = "api/v1/profile"
    const val UPDATE_PROFILE = "api/v1/profile/update"
    const val DELETE_ACCOUNT = "api/v1/account/delete"
    
    const val GET_WALLET_BALANCE = "api/v1/wallet/balance"
    const val GET_TRANSACTIONS = "api/v1/wallet/transactions"
    const val TOP_UP_WALLET = "api/v1/wallet/topup"
    
    const val GET_AWARDS = "api/v1/awards"
    const val GET_LANGUAGES = "api/v1/languages"
}
