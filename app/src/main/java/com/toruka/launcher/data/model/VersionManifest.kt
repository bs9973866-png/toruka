package com.toruka.launcher.data.model

import com.google.gson.annotations.SerializedName

/**
 * Mojang version manifest response
 */
data class VersionManifest(
    @SerializedName("latest")
    val latest: LatestVersions,
    @SerializedName("versions")
    val versions: List<VersionInfo>
)

data class LatestVersions(
    @SerializedName("release")
    val release: String,
    @SerializedName("snapshot")
    val snapshot: String
)

data class VersionInfo(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("releaseTime")
    val releaseTime: String
) {
    val isRelease: Boolean get() = type == "release"
    val isSnapshot: Boolean get() = type == "snapshot"
    val isBeta: Boolean get() = type == "old_beta"
    val isAlpha: Boolean get() = type == "old_alpha"
}

enum class VersionType(val displayName: String) {
    RELEASE("Релиз"),
    SNAPSHOT("Снапшот"),
    BETA("Beta"),
    ALPHA("Alpha"),
    UNKNOWN("Неизвестно");

    companion object {
        fun fromString(type: String): VersionType = when (type) {
            "release" -> RELEASE
            "snapshot" -> SNAPSHOT
            "old_beta" -> BETA
            "old_alpha" -> ALPHA
            else -> UNKNOWN
        }
    }
}
