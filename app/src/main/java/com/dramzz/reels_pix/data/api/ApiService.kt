package com.dramzz.reels_pix.data.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun get(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @QueryMap params: Map<String, String> = emptyMap()
    ): Response<JsonObject>

    @POST
    suspend fun post(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @Body body: Any? = null
    ): Response<JsonObject>

    @FormUrlEncoded
    @POST
    suspend fun postForm(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @FieldMap params: Map<String, String> = emptyMap()
    ): Response<JsonObject>

    @Multipart
    @POST
    suspend fun postMultipart(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody> = emptyMap(),
        @Part parts: List<MultipartBody.Part>
    ): Response<JsonObject>

    @PUT
    suspend fun put(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @Body body: Any? = null
    ): Response<JsonObject>

    @DELETE
    suspend fun delete(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
        @QueryMap params: Map<String, String> = emptyMap()
    ): Response<JsonObject>
}
