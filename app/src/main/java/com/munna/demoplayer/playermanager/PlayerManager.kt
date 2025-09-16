package com.munna.demoplayer.playermanager

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import okhttp3.OkHttpClient
import java.io.File

object PlayerManager {
    private lateinit var appContext: Context
    private var exoPlayer: ExoPlayer? = null
    private var cache: SimpleCache? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        if (cache == null) {
            val cacheDir = File(appContext.cacheDir, "media_cache")
            val evictor = LeastRecentlyUsedCacheEvictor(200L * 1024L * 1024L) // 200MB
            cache = SimpleCache(cacheDir, evictor)
        }
    }

    fun getPlayer(): ExoPlayer {
        return exoPlayer ?: ExoPlayer.Builder(appContext).build().also { exoPlayer = it }
    }

    fun buildMediaSource(url: String): ProgressiveMediaSource {
        val okHttpClient = OkHttpClient()
        val upstream = OkHttpDataSource.Factory(okHttpClient)
        val cacheDsFactory = CacheDataSource.Factory()
            .setCache(cache!!)
            .setUpstreamDataSourceFactory(upstream)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        return ProgressiveMediaSource.Factory(cacheDsFactory)
            .createMediaSource(MediaItem.fromUri(url))
    }
}