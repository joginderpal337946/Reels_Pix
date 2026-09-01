package com.dramzz.reels_pix.data.api

import com.dramzz.reels_pix.base.local.SessionManager
import com.dramzz.reels_pix.utils.AuthEventBus
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class ApiHelperImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ApiHelper {

    override suspend fun callApi(
        url: String,
        method: Constants.ApiType,
        headers: Map<String, String>,
        params: Map<String, Any>,
        body: Any?,
        isFormData: Boolean,
        useToken: Boolean,
        parts: List<MultipartBody.Part>?
    ): Response<JsonObject> {

        val finalHeaders = headers.toMutableMap()
        finalHeaders["Accept"] = "application/json"
        if (useToken) {
            val token = sessionManager.getToken()
            if (!token.isNullOrEmpty()) {
                finalHeaders["Authorization"] = "Bearer $token"
            }
        }

        val stringParams = params.mapValues { it.value.toString() }

        return try {
            val result = when (method) {
                Constants.ApiType.GET -> {
                    apiService.get(url, finalHeaders, stringParams)
                }
                Constants.ApiType.POST -> {
                    if (parts != null && parts.isNotEmpty()) {
                        val requestBodyParams = stringParams.mapValues {
                            it.value.toRequestBody(MultipartBody.FORM)
                        }
                        apiService.postMultipart(url, finalHeaders, requestBodyParams, parts)
                    } else if (isFormData) {
                        apiService.postForm(url, finalHeaders, stringParams)
                    } else {
                        val requestBody = body ?: if (stringParams.isNotEmpty()) stringParams else null
                        apiService.post(url, finalHeaders, requestBody)
                    }
                }
                Constants.ApiType.PUT -> {
                    val requestBody = body ?: if (stringParams.isNotEmpty()) stringParams else null
                    apiService.put(url, finalHeaders, requestBody)
                }
                Constants.ApiType.DELETE -> {
                    apiService.delete(url, finalHeaders, stringParams)
                }
                Constants.ApiType.PATCH -> {
                    val requestBody = body ?: if (stringParams.isNotEmpty()) stringParams else null
                    apiService.post(url, finalHeaders, requestBody)
                }
            }

            if (result.isSuccessful) {
                val body = result.body()
                if (body != null && body.has("status") && body.get("status").asInt == 401) {
                    sessionManager.clear()
                    AuthEventBus.emitLogoutEvent()
                }
                Response.success(body)
            } else {
                if (result.code() == 401) {
                    sessionManager.clear()
                    AuthEventBus.emitLogoutEvent()
                } else {
                    // Sometimes APIs return HTTP 403 or 400 with status 401 in body.
                    // To avoid consuming errorBody stream, we'll rely on result.code() for now.
                    // But if it is 401, we already intercepted it.
                }
                Response.error(result.code(), result.errorBody() ?: okhttp3.ResponseBody.create(null, "Error"))
            }
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, e.message ?: "Unknown error"))
        }
    }
}

