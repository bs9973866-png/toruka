package com.toruka.launcher.data.model

sealed class DownloadState {
    data object Idle : DownloadState()
    data class Downloading(
        val progress: Float,
        val bytesDownloaded: Long,
        val totalBytes: Long,
        val speed: Long = 0
    ) : DownloadState()
    data class Installing(val progress: Float) : DownloadState()
    data object Completed : DownloadState()
    data class Error(val message: String) : DownloadState()
}

sealed class LaunchState {
    data object Idle : LaunchState()
    data class Preparing(val message: String) : LaunchState()
    data object Launching : LaunchState()
    data class Error(val message: String) : LaunchState()
}

sealed class ResourceType {
    data class VersionJson(val versionId: String) : ResourceType()
    data class ClientJar(val versionId: String) : ResourceType()
    data class Asset(val hash: String) : ResourceType()
    data class Library(val path: String) : ResourceType()
    data class NativeLibrary(val path: String) : ResourceType()
    data class JavaRuntime(val version: String, val os: String, val arch: String) : ResourceType()
}
