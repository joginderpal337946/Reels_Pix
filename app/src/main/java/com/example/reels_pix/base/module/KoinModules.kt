package com.example.reels_pix.base.module

import com.example.reels_pix.base.connectivity.ConnectivityProvider
import com.example.reels_pix.base.connectivity.ConnectivityProviderImpl
import com.example.reels_pix.base.local.DataStoreManager
import com.example.reels_pix.data.api.ApiHelper
import com.example.reels_pix.data.api.ApiHelperImpl
import com.example.reels_pix.data.api.ApiService
import com.example.reels_pix.data.api.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val baseModule = module {
    single<ConnectivityProvider> { ConnectivityProviderImpl(androidContext()) }
    single { DataStoreManager(androidContext()) }
}

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    single { get<Retrofit>().create(ApiService::class.java) }
    single<ApiHelper> { ApiHelperImpl(get()) }
}

val appModule = module {
    // App specific modules like ViewModels
}
