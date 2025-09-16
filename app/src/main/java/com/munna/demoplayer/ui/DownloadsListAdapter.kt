package com.munna.demoplayer.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.munna.demoplayer.data.db.DownloadEntity
import com.munna.demoplayer.databinding.ItemDownloadedVideoBinding
import com.munna.demoplayer.databinding.ItemVideoBinding
import com.munna.demoplayer.playermanager.PlayerManager

class DownloadsListAdapter(private val context:Context,
    private val onClick: (DownloadEntity) -> Unit
) : ListAdapter<DownloadEntity, DownloadsListAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDownloadedVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Reuse layout; hide download button for downloaded items
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: ItemDownloadedVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entity: DownloadEntity) {
            binding.title.text = entity.title + " (offline)"
            Glide.with(context).load(entity.thumbNail).into(binding.imageView)
            itemView.setOnClickListener { onClick(entity) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<DownloadEntity>() {
            override fun areItemsTheSame(oldItem: DownloadEntity, newItem: DownloadEntity) = oldItem.url == newItem.url
            override fun areContentsTheSame(oldItem: DownloadEntity, newItem: DownloadEntity) = oldItem == newItem
        }
    }
}