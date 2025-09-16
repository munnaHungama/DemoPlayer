package com.munna.demoplayer.data.api

import com.munna.demoplayer.models.VideoItem
import com.munna.demoplayer.models.VideoResponse
import retrofit2.http.GET

interface VideoApi{
    @GET("poudyalanil/ca84582cbeb4fc123a13290a586da925/raw/14a27bd0bcd0cd323b35ad79cf3b493dddf6216b/videos.json")
    suspend fun getVideos(): List<VideoItem>

    companion object {
        const val BASE_URL = "https://gist.githubusercontent.com/"
    }
}