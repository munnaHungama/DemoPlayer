package com.munna.demoplayer.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.munna.demoplayer.data.repo.VideoRepository
import com.munna.demoplayer.models.VideoItem
import kotlinx.coroutines.flow.Flow

class ShortsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = VideoRepository(app)
    val videos: Flow<PagingData<VideoItem>> = repo.pagedVideos().cachedIn(viewModelScope)
}