package com.dramzz.reels_pix.base.module

import android.content.Context
import android.content.SharedPreferences
import com.dramzz.reels_pix.base.connectivity.ConnectivityProvider
import com.dramzz.reels_pix.base.connectivity.ConnectivityProviderImpl
import com.dramzz.reels_pix.base.local.SessionManager
import com.dramzz.reels_pix.base.permission.PermissionHandler
import com.dramzz.reels_pix.base.permission.PermissionHandlerImpl
import com.dramzz.reels_pix.data.api.ApiHelper
import com.dramzz.reels_pix.data.api.ApiHelperImpl
import com.dramzz.reels_pix.data.api.ApiService
import com.dramzz.reels_pix.data.api.Constants
import com.dramzz.reels_pix.main.MainViewModel
import com.dramzz.reels_pix.ui.dashboard.home.HomeViewModel
import com.dramzz.reels_pix.ui.dashboard.feed.FeedViewModel
import com.dramzz.reels_pix.ui.dashboard.mylist.MyListViewModel
import com.dramzz.reels_pix.ui.dashboard.awards.AwardsViewModel
import com.dramzz.reels_pix.ui.dashboard.profile.ProfileViewModel
import com.dramzz.reels_pix.ui.dashboard.wallet.WalletViewModel
import com.dramzz.reels_pix.ui.dashboard.search.SearchViewModel
import com.dramzz.reels_pix.ui.dashboard.category.CategoryViewModel
import com.dramzz.reels_pix.ui.dashboard.videoplayer.VideoPlayerViewModel
import com.dramzz.reels_pix.utils.ConnectivityObserver
import com.dramzz.reels_pix.utils.NetworkConnectivityObserver
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single<SharedPreferences> { androidContext().getSharedPreferences("dramazz_prefs", Context.MODE_PRIVATE) }
    single { SessionManager(get()) }
    single<ConnectivityProvider> { ConnectivityProviderImpl(androidContext()) }
    single<PermissionHandler> { PermissionHandlerImpl(androidContext()) }
    single<ConnectivityObserver> { NetworkConnectivityObserver(androidContext()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { FeedViewModel(get(), get()) }
    viewModel { MyListViewModel() }
    viewModel { AwardsViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { com.dramzz.reels_pix.ui.dashboard.profile.SignInViewModel(get(), get()) }
    viewModel { com.dramzz.reels_pix.ui.dashboard.profile.InvitationViewModel(get(), get()) }
    viewModel { WalletViewModel() }
    viewModel { SearchViewModel(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { VideoPlayerViewModel(get(), get()) }
    viewModel { com.dramzz.reels_pix.ui.dashboard.mylist.SavedSeriesViewModel(get(), get()) }
    viewModel { com.dramzz.reels_pix.ui.dashboard.mylist.HistoryViewModel(get(), get()) }
}

val networkModule = module {

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(ApiService::class.java) }

    single<ApiHelper> { ApiHelperImpl(get(), get()) }
}

