package com.munna.demoplayer.data.repo

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.munna.demoplayer.data.api.VideoApi
import com.munna.demoplayer.data.db.AppDatabase
import com.munna.demoplayer.data.db.DownloadEntity
import com.munna.demoplayer.data.paging.VideosPagingSource
import com.munna.demoplayer.models.VideoItem
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VideoRepository(context: Context) {
    private val api: VideoApi
    private val db = AppDatabase.get(context)

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        api = Retrofit.Builder()
            .baseUrl(VideoApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(VideoApi::class.java)
    }

    fun pagedVideos(pageSize: Int = 6): Flow<PagingData<VideoItem>> =
        Pager(PagingConfig(pageSize = pageSize, prefetchDistance = 2)) {
            VideosPagingSource(api)
        }.flow

    suspend fun findDownload(url: String) = db.downloads().find(url)
    fun observeDownloads() = db.downloads().observeAll()
    suspend fun saveDownload(entity: DownloadEntity) = db.downloads().upsert(entity)
}