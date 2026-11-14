package com.toruka.launcher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey
    val id: String,
    val type: AccountType,
    val username: String,
    val displayName: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresAt: Long = 0,
    val uuid: String? = null,
    val skinUrl: String? = null,
    val isSelected: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AccountType(val displayName: String) {
    MICROSOFT("Microsoft"),
    MOJANG("Mojang"),
    OFFLINE("Офлайн");

    companion object {
        fun fromString(value: String): AccountType = when (value.uppercase()) {
            "MICROSOFT" -> MICROSOFT
            "MOJANG" -> MOJANG
            else -> OFFLINE
        }
    }
}

// Microsoft OAuth flow models
data class MicrosoftTokenResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int,
    val token_type: String
)

data class XboxLiveAuthResponse(
    val Token: String,
    val DisplayClaims: DisplayClaims
)

data class DisplayClaims(
    val xui: List<XuiClaim>
)

data class XuiClaim(
    val uhs: String
)

data class MinecraftAuthResponse(
    val username: String,
    val access_token: String,
    val token_type: String
)

data class MinecraftProfileResponse(
    val id: String,
    val name: String,
    val skins: List<SkinInfo>? = null
)

data class SkinInfo(
    val id: String,
    val state: String,
    val url: String,
    val variant: String
)
