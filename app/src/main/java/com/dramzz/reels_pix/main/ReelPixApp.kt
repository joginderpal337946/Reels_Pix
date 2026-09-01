package com.dramzz.reels_pix.main

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.dramzz.reels_pix.base.module.appModule
import com.dramzz.reels_pix.base.module.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ReelPixApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Configure Coil with aggressive disk and memory caching
        val imageLoader = ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Use 25% of available RAM
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02) // Use 2% of disk space
                    .build()
            }
            .crossfade(true)
            .build()
        Coil.setImageLoader(imageLoader)

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ReelPixApp)
            modules(
                appModule,
                networkModule
            )
        }
    }
}