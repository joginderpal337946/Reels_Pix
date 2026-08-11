package com.example.reels_pix.data.api

import com.example.reels_pix.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @GET("dummy-url")
    suspend fun getDummyData(): Response<DummyModel>
    
    @POST(Constants.LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<AuthResponse>>

    @GET(Constants.GET_PROFILE)
    suspend fun getProfile(): Response<BaseResponse<UserProfile>>
    
    @PUT(Constants.UPDATE_PROFILE)
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<BaseResponse<UserProfile>>
    
    @DELETE(Constants.DELETE_ACCOUNT)
    suspend fun deleteAccount(): Response<GenericResponse>

    @GET(Constants.GET_WALLET_BALANCE)
    suspend fun getWalletBalance(): Response<BaseResponse<String>>

    @GET(Constants.GET_TRANSACTIONS)
    suspend fun getTransactions(): Response<BaseResponse<List<HistoryItem>>>

    @POST(Constants.TOP_UP_WALLET)
    suspend fun topUpWallet(@Body request: TopUpPackage): Response<GenericResponse>
    
    @GET(Constants.GET_AWARDS)
    suspend fun getAwards(): Response<BaseResponse<List<RewardTask>>>
    
    @GET(Constants.GET_LANGUAGES)
    suspend fun getLanguages(): Response<BaseResponse<List<String>>>
}
