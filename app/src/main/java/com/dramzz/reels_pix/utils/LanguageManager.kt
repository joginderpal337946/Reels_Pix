package com.dramzz.reels_pix.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

class LanguageManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("dramazz_language_prefs", Context.MODE_PRIVATE)

    fun setLanguage(languageCode: String) {
        prefs.edit().putString("language_code", languageCode).apply()
    }

    fun getLanguage(): String {
        return prefs.getString("language_code", "en") ?: "en"
    }

    companion object {
        fun updateContextLocale(context: Context, languageCode: String): Context {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)

            return context.createConfigurationContext(config)
        }
        
        fun getLanguageCodeFromPrefs(context: Context): String {
            val prefs = context.getSharedPreferences("dramazz_language_prefs", Context.MODE_PRIVATE)
            return prefs.getString("language_code", "en") ?: "en"
        }
    }
}
