package com.munna.demoplayer.download

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.munna.demoplayer.data.db.AppDatabase

class DownloadsViewModel (app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.get(app)
    val downloads = db.downloads().observeAll()
}