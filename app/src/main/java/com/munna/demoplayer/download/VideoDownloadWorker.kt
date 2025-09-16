package com.munna.demoplayer.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.munna.demoplayer.data.db.AppDatabase
import com.munna.demoplayer.data.db.DownloadEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class VideoDownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val url = inputData.getString(KEY_URL) ?: return@withContext Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: "Video"
        val thumbnail = inputData.getString(THUMB_NAIL) ?: ""

        try {
            val client = OkHttpClient()
            val req = Request.Builder().url(url).build()
            val res = client.newCall(req).execute()
            if (!res.isSuccessful) return@withContext Result.retry()

            val dir = File(applicationContext.filesDir, "downloads").apply { mkdirs() }
            val file = File(dir, title.filter { it.isLetterOrDigit() || it == '_' } + ".mp4")
            res.body?.byteStream().use { input ->
                FileOutputStream(file).use { output ->
                    if (input != null) input.copyTo(output)
                }
            }

            AppDatabase.get(applicationContext).downloads()
                .upsert(DownloadEntity(url = url, title = title, filePath = file.absolutePath, thumbNail = thumbnail))

            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_URL = "url"
        const val KEY_TITLE = "title"
        const val THUMB_NAIL = "thumbNail"
    }
}