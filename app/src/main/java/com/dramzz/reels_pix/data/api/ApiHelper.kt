package com.dramzz.reels_pix.data.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response

interface ApiHelper {
    suspend fun callApi(
        url: String,
        method: Constants.ApiType = Constants.ApiType.GET,
        headers: Map<String, String> = emptyMap(),
        params: Map<String, Any> = emptyMap(),
        body: Any? = null,
        isFormData: Boolean = false,
        useToken: Boolean = true,
        parts: List<MultipartBody.Part>? = null
    ): Response<JsonObject>
}
