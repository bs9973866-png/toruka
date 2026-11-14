package com.toruka.launcher

import android.app.NativeActivity
import android.os.Bundle
import android.view.View
import com.toruka.launcher.jni.NativeBridge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : NativeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val versionId = intent.getStringExtra("version_id") ?: return finish()
        val javaPath = intent.getStringExtra("java_path") ?: return finish()
        val gameDir = intent.getStringExtra("game_dir") ?: return finish()
        val accountJson = intent.getStringExtra("account") ?: return finish()
        
        NativeBridge.setupGameEnvironment(
            versionId = versionId,
            javaPath = javaPath,
            gameDir = gameDir,
            accountJson = accountJson
        )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
        }
    }

    companion object {
        const val EXTRA_VERSION_ID = "version_id"
        const val EXTRA_JAVA_PATH = "java_path"
        const val EXTRA_GAME_DIR = "game_dir"
        const val EXTRA_ACCOUNT = "account"
    }
}
