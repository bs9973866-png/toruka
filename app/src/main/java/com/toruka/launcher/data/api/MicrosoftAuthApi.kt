package com.toruka.launcher.data.api

import com.toruka.launcher.data.model.MicrosoftTokenResponse
import com.toruka.launcher.data.model.MinecraftAuthResponse
import com.toruka.launcher.data.model.MinecraftProfileResponse
import com.toruka.launcher.data.model.XboxLiveAuthResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface MicrosoftAuthApi {
    
    @POST("consumers/oauth2/v2.0/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("code") code: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("redirect_uri") redirectUri: String,
        @Field("scope") scope: String = "XboxLive.signin offline_access"
    ): MicrosoftTokenResponse
    
    @POST("consumers/oauth2/v2.0/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("scope") scope: String = "XboxLive.signin offline_access"
    ): MicrosoftTokenResponse
    
    companion object {
        const val BASE_URL = "https://login.microsoftonline.com/"
        const val CLIENT_ID = "00000000402b5328" // Minecraft client ID
        const val REDIRECT_URI = "https://login.live.com/oauth20_desktop.srf"
    }
}

interface XboxLiveApi {
    
    @POST("user/authenticate")
    suspend fun authenticateXbox(
        @Header("Content-Type") contentType: String = "application/json",
        @retrofit2.http.Body body: Map<String, Any>
    ): XboxLiveAuthResponse
    
    @POST("XSTS/authorize")
    suspend fun authorizeXSTS(
        @Header("Content-Type") contentType: String = "application/json",
        @retrofit2.http.Body body: Map<String, Any>
    ): XboxLiveAuthResponse
    
    companion object {
        const val BASE_URL = "https://user.auth.xboxlive.com/"
        const val XSTS_BASE_URL = "https://xsts.auth.xboxlive.com/"
    }
}

interface MinecraftApi {
    
    @POST("authentication/login_with_xbox")
    suspend fun loginWithXbox(
        @retrofit2.http.Body body: Map<String, String>
    ): MinecraftAuthResponse
    
    @GET("minecraft/profile")
    suspend fun getProfile(
        @Header("Authorization") authorization: String
    ): MinecraftProfileResponse
    
    companion object {
        const val BASE_URL = "https://api.minecraftservices.com/"
    }
}

// Inline GET for MinecraftApi
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GET(val value: String)
