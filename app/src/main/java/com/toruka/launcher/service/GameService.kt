package com.toruka.launcher.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.toruka.launcher.jni.NativeBridge

class GameService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Monitor game process
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        NativeBridge.cleanup()
    }
}
