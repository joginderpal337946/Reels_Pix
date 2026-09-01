package com.dramzz.reels_pix.base.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dramzz.reels_pix.data.model.RegisterData

class SessionManager(private val sharedPreferences: SharedPreferences) {

    object KEY {
        const val USER_DATA = "user_data"
        const val TOKEN = "token"
        const val SPLASH_SHOWN = "splash_shown"
    }

    fun setUserData(data: RegisterData?) {
        val editor = sharedPreferences.edit()

        // login data saved
        if (data != null) {
            editor.putString(KEY.USER_DATA, Gson().toJson(data))
        } else {
            editor.remove(KEY.USER_DATA)
        }
        editor.apply()
        data?.accessToken
            ?.takeIf { it.isNotBlank() }
            ?.let { setToken(it) }
    }

    fun getUserData(): RegisterData? {
        val gson = Gson()
        val json: String? = sharedPreferences.getString(KEY.USER_DATA, "")
        if (json.isNullOrEmpty()) return null
        return try {
            gson.fromJson(json, RegisterData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun setToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY.TOKEN, "")
    }

    fun setSplashShown(isShown: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY.SPLASH_SHOWN, isShown)
        editor.apply()
    }

    fun isSplashShown(): Boolean {
        return sharedPreferences.getBoolean(KEY.SPLASH_SHOWN, false)
    }

    internal inline fun <reified T> saveList(key: String, list: List<T>) {
        val gson = Gson()
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }

    internal inline fun <reified T> getList(key: String): List<T> {
        val gson = Gson()
        val json = sharedPreferences.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearList(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun clear() {
        val wasSplashShown = isSplashShown()
        sharedPreferences.edit().clear().apply()
        setSplashShown(wasSplashShown)
    }
}
