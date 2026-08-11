package com.example.reels_pix.data.api

import com.example.reels_pix.data.model.*
import retrofit2.Response

interface ApiHelper {
    suspend fun getDummyData(): Response<DummyModel>
    
    suspend fun login(request: LoginRequest): Response<BaseResponse<AuthResponse>>
    suspend fun getProfile(): Response<BaseResponse<UserProfile>>
    suspend fun updateProfile(request: UpdateProfileRequest): Response<BaseResponse<UserProfile>>
    suspend fun deleteAccount(): Response<GenericResponse>
    suspend fun getWalletBalance(): Response<BaseResponse<String>>
    suspend fun getTransactions(): Response<BaseResponse<List<HistoryItem>>>
    suspend fun topUpWallet(request: TopUpPackage): Response<GenericResponse>
    suspend fun getAwards(): Response<BaseResponse<List<RewardTask>>>
    suspend fun getLanguages(): Response<BaseResponse<List<String>>>
}
