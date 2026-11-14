package com.toruka.launcher.data.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadApi {
    
    @GET
    @Streaming
    suspend fun downloadFile(
        @Url url: String,
        @Header("Range") range: String? = null
    ): ResponseBody
    
    @GET
    suspend fun getFileSize(
        @Url url: String
    ): retrofit2.Response<Void>
}
