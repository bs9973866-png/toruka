package com.toruka.launcher.data.api

import com.toruka.launcher.data.model.VersionDetail
import com.toruka.launcher.data.model.VersionManifest
import retrofit2.http.GET
import retrofit2.http.Url

interface MojangApi {
    
    @GET("mc/game/version_manifest.json")
    suspend fun getVersionManifest(): VersionManifest
    
    @GET
    suspend fun getVersionDetail(@Url url: String): VersionDetail
    
    companion object {
        const val BASE_URL = "https://piston-meta.mojang.com/"
        const val RESOURCES_URL = "https://piston-data.mojang.com/"
        const val LIBRARIES_URL = "https://libraries.minecraft.net/"
        const val ASSETS_URL = "https://piston-data.mojang.com/assets/"
    }
}
