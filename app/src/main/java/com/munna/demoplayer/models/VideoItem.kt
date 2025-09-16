package com.munna.demoplayer.models

data class VideoItem(
    val id: String="null",
    val title: String="",
    val thumbnailUrl: String="",
    val duration: String="",
    val uploadTime: String="",
    val views: String="",
    val author : String="",
    val videoUrl : String="",
    val description : String="",
    val subscriber : String="",
    val isLive: Boolean = false
) {
    val url: String? get() = videoUrl
}