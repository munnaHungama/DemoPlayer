package com.munna.demoplayer

import android.app.Application
import com.munna.demoplayer.playermanager.PlayerManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        PlayerManager.initialize(this)
    }
}