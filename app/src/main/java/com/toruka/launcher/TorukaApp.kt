package com.toruka.launcher

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TorukaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TorukaApp
            private set
    }
}
