package com.dramzz.reels_pix.main

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import com.dramzz.reels_pix.ui.components.NoInternetBottomSheet
import com.dramzz.reels_pix.ui.dashboard.home.DashboardScreen
import com.dramzz.reels_pix.ui.theme.Reels_PixTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity :  FragmentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val languageCode = com.dramzz.reels_pix.utils.LanguageManager.getLanguageCodeFromPrefs(newBase)
        val locale = java.util.Locale(languageCode)
        java.util.Locale.setDefault(locale)

        val res = newBase.resources
        val configuration = Configuration(res.configuration).apply {
            setLocale(locale)
            fontScale = 1.0f
            densityDpi = res.displayMetrics.densityDpi
        }
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            val viewModel: MainViewModel = koinViewModel()
            Reels_PixTheme {
                val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
                androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    DashboardScreen()
                    
                    if (!isNetworkAvailable) {
                        NoInternetBottomSheet(
                            isVisible = true
                        )
                    }
                }
            }
        }
    }
}