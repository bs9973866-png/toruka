package com.toruka.launcher.jni

import android.util.Log

object NativeBridge {
    
    init {
        try {
            System.loadLibrary("toruka_bridge")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("NativeBridge", "Failed to load native library", e)
        }
    }

    external fun setupGameEnvironment(
        versionId: String,
        javaPath: String,
        gameDir: String,
        accountJson: String
    ): Boolean

    external fun launchJavaVM(
        javaPath: String,
        classpath: String,
        mainClass: String,
        args: Array<String>
    ): Int

    external fun setWindowPointer(window: Long)
    external fun handleTouchEvent(action: Int, x: Float, y: Float, pointerId: Int)
    external fun handleKeyEvent(keyCode: Int, action: Int, modifiers: Int)
    external fun resizeWindow(width: Int, height: Int)
    external fun cleanup()

    // LWJGL bridge functions
    external fun lwjglSetContext(window: Long)
    external fun lwjglSwapBuffers()
    external fun lwjglMakeCurrent(window: Long)
    external fun lwjglGetProcAddress(name: String): Long

    // GL4ES/VirGL bridge
    external fun initGLBridge(renderer: String)
    external fun setGLAttribute(attr: Int, value: Int)
}
