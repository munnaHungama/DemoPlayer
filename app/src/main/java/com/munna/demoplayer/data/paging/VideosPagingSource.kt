package com.munna.demoplayer.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.munna.demoplayer.data.api.VideoApi
import com.munna.demoplayer.models.VideoItem

class VideosPagingSource(
    private val api: VideoApi
) : PagingSource<Int, VideoItem>() {

    private var cached: List<VideoItem>? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoItem> {
        val page = params.key ?: 0
        return try {
            val list = cached ?: run {
                val res = api.getVideos()
                Log.e("API_response", "$res")
                val videos = res
                cached = videos
                videos
            }
            val pageSize = params.loadSize.coerceAtMost(8)
            val from = page * pageSize
            val to = (from + pageSize).coerceAtMost(list.size)
            val sub = if (from < to) list.subList(from, to) else emptyList()
            val nextKey = if (to < list.size) page + 1 else null
            LoadResult.Page(
                data = sub,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Log.e("apiDataError","${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, VideoItem>): Int? =
        state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
}