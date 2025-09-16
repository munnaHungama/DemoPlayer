package com.munna.demoplayer.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val url: String,
    val title: String,
    val filePath: String,
    val thumbNail: String,
    val createdAt: Long = System.currentTimeMillis()
)