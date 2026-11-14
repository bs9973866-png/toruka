package com.toruka.launcher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.toruka.launcher.data.local.Converters

@Entity(tableName = "profiles")
@TypeConverters(Converters::class)
data class Profile(
    @PrimaryKey
    val id: String,
    val name: String,
    val versionId: String,
    val gameDir: String,
    val javaArgs: String = "",
    val gameArgs: String = "",
    val memoryAllocation: Int = 2048,
    val renderer: RendererType = RendererType.GL4ES,
    val resolutionWidth: Int = 0,
    val resolutionHeight: Int = 0,
    val fullscreen: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastPlayed: Long = 0,
    val playCount: Int = 0,
    val isFavorite: Boolean = false
)

enum class RendererType(val displayName: String, val description: String) {
    GL4ES("GL4ES", "OpenGL 2.1 - максимальная совместимость"),
    VIRGL("VirGL", "OpenGL 4.3 - лучшая производительность на новых GPU"),
    ZINK("Zink", "Vulkan - экспериментальный"),
    ANGLE("ANGLE", "Direct3D/Vulkan - для современных устройств");

    companion object {
        fun fromOrdinal(ordinal: Int): RendererType = entries.getOrElse(ordinal) { GL4ES }
    }
}
