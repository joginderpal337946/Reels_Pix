package com.dramzz.reels_pix.data.api

import com.dramzz.reels_pix.BuildConfig

object Constants {
    const val BASE_URL = BuildConfig.BASE_URL
    const val BASE_MAIN_URL = BuildConfig.BASE_MAIN_URL
    const val TIMEOUT_SECONDS = 60L

    // Dummy API Endpoints
    const val LOGIN ="social-login"
    const val LOGOUT ="logout"
    const val PROFILE ="profile"
    const val PRIVACY_POLICY ="privacy-policy"
    const val USER_AGREEMENT ="user-agreement"
    const val REWARDS ="rewards"
    const val CLAIM_REWARD ="rewards/claim"
    const val FEED ="feed"
    const val HOME ="home"
    const val SERIES_HISTORY = "series-history"

    enum class ApiType {
        GET, POST, PUT, DELETE, PATCH
    }
}
