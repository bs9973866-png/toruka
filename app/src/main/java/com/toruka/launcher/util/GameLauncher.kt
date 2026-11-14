package com.toruka.launcher.util

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.toruka.launcher.GameActivity
import com.toruka.launcher.data.model.Account
import com.toruka.launcher.data.model.Profile
import com.toruka.launcher.data.model.VersionDetail
import java.io.File

object GameLauncher {

    fun launchGame(
        context: Context,
        profile: Profile,
        account: Account,
        versionDetail: VersionDetail,
        gameDir: File
    ) {
        val intent = Intent(context, GameActivity::class.java).apply {
            putExtra(GameActivity.EXTRA_VERSION_ID, profile.versionId)
            putExtra(GameActivity.EXTRA_JAVA_PATH, getJavaPath(context))
            putExtra(GameActivity.EXTRA_GAME_DIR, gameDir.absolutePath)
            putExtra(GameActivity.EXTRA_ACCOUNT, Gson().toJson(account))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(intent)
    }

    fun buildClasspath(versionDetail: VersionDetail, gameDir: File): String {
        val librariesDir = File(gameDir, "libraries")
        val versionDir = File(gameDir, "versions/${versionDetail.id}")
        
        val classPath = StringBuilder()
        
        // Add client jar
        classPath.append(File(versionDir, "${versionDetail.id}.jar").absolutePath)
        
        // Add libraries
        versionDetail.libraries.filter { it.isAllowed() }.forEach { library ->
            val libFile = File(librariesDir, library.getArtifactPath())
            if (libFile.exists()) {
                classPath.append(File.pathSeparator)
                classPath.append(libFile.absolutePath)
            }
        }
        
        return classPath.toString()
    }

    fun buildJvmArguments(
        profile: Profile,
        gameDir: File,
        nativesDir: File,
        classpath: String
    ): List<String> {
        val args = mutableListOf<String>()
        
        // Memory
        args.add("-Xms${profile.memoryAllocation}M")
        args.add("-Xmx${profile.memoryAllocation}M")
        
        // System properties
        args.add("-Djava.library.path=${nativesDir.absolutePath}")
        args.add("-Dminecraft.launcher.brand=Toruka")
        args.add("-Dminecraft.launcher.version=1.0.0")
        
        // Classpath
        args.add("-cp")
        args.add(classpath)
        
        // Custom JVM args
        if (profile.javaArgs.isNotEmpty()) {
            args.addAll(profile.javaArgs.split(" "))
        }
        
        return args
    }

    fun buildGameArguments(
        profile: Profile,
        account: Account,
        gameDir: File,
        assetIndex: String
    ): List<String> {
        val args = mutableListOf<String>()
        
        args.add("--username")
        args.add(account.displayName ?: account.username)
        
        args.add("--version")
        args.add(profile.versionId)
        
        args.add("--gameDir")
        args.add(gameDir.absolutePath)
        
        args.add("--assetsDir")
        args.add(File(gameDir, "assets").absolutePath)
        
        args.add("--assetIndex")
        args.add(assetIndex)
        
        args.add("--uuid")
        args.add(account.uuid ?: "")
        
        args.add("--accessToken")
        args.add(account.accessToken ?: "0")
        
        args.add("--userType")
        args.add(if (account.type == com.toruka.launcher.data.model.AccountType.MICROSOFT) "msa" else "legacy")
        
        args.add("--versionType")
        args.add("release")
        
        // Custom game args
        if (profile.gameArgs.isNotEmpty()) {
            args.addAll(profile.gameArgs.split(" "))
        }
        
        return args
    }

    private fun getJavaPath(context: Context): String {
        val javaDir = File(context.filesDir, "java/jre17/bin/java")
        return if (javaDir.exists()) javaDir.absolutePath else "/system/bin/java"
    }
}
