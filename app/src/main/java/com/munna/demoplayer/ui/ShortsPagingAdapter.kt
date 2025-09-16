package com.munna.demoplayer.ui

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.munna.demoplayer.databinding.ItemVideoBinding
import com.munna.demoplayer.download.VideoDownloadWorker
import com.munna.demoplayer.models.VideoItem
import com.munna.demoplayer.playermanager.PlayerManager

class ShortsPagingAdapter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) : PagingDataAdapter<VideoItem, ShortsPagingAdapter.VideoVH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoVH {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoVH(binding)
    }

    override fun onViewDetachedFromWindow(holder: VideoVH) {
        super.onViewDetachedFromWindow(holder)
        holder.binding.playerView.player = null
    }

    fun itemAt(position: Int): VideoItem? = getItem(position)

    override fun onBindViewHolder(holder: VideoVH, position: Int) {
        val item = getItem(position) ?: return
        holder.binding.title.text = item.title
        holder.binding.playerView.player = null            // ✅ don’t attach the player here
        holder.itemView.tag = item                         // quick access from fragment

        val url = item.url ?: return
        holder.binding.downloadBtn.setOnClickListener {
            val work = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(VideoDownloadWorker.KEY_URL, url)
                        .putString(VideoDownloadWorker.THUMB_NAIL, item.thumbnailUrl)
                        .putString(VideoDownloadWorker.KEY_TITLE, item.title)
                        .build()
                ).build()
            WorkManager.getInstance(context).enqueue(work)
        }
    }
    override fun onViewRecycled(holder: VideoVH) {
        holder.binding.playerView.player = null            // ✅ detach when recycled
        super.onViewRecycled(holder)
    }

    inner class VideoVH(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<VideoItem>() {
            override fun areItemsTheSame(o: VideoItem, n: VideoItem) = o.url == n.url
            override fun areContentsTheSame(o: VideoItem, n: VideoItem) = o == n
        }
    }
}