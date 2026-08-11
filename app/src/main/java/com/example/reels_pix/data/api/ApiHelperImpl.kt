package com.example.reels_pix.data.api

import com.example.reels_pix.data.model.*
import retrofit2.Response

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {
    override suspend fun getDummyData(): Response<DummyModel> = apiService.getDummyData()
    override suspend fun login(request: LoginRequest): Response<BaseResponse<AuthResponse>> = apiService.login(request)
    override suspend fun getProfile(): Response<BaseResponse<UserProfile>> = apiService.getProfile()
    override suspend fun updateProfile(request: UpdateProfileRequest): Response<BaseResponse<UserProfile>> = apiService.updateProfile(request)
    override suspend fun deleteAccount(): Response<GenericResponse> = apiService.deleteAccount()
    override suspend fun getWalletBalance(): Response<BaseResponse<String>> = apiService.getWalletBalance()
    override suspend fun getTransactions(): Response<BaseResponse<List<HistoryItem>>> = apiService.getTransactions()
    override suspend fun topUpWallet(request: TopUpPackage): Response<GenericResponse> = apiService.topUpWallet(request)
    override suspend fun getAwards(): Response<BaseResponse<List<RewardTask>>> = apiService.getAwards()
    override suspend fun getLanguages(): Response<BaseResponse<List<String>>> = apiService.getLanguages()
}
