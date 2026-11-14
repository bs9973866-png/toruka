package com.toruka.launcher.data.repository

import com.toruka.launcher.data.api.MojangApi
import com.toruka.launcher.data.model.VersionDetail
import com.toruka.launcher.data.model.VersionInfo
import com.toruka.launcher.data.model.VersionManifest
import com.toruka.launcher.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VersionRepository @Inject constructor(
    private val mojangApi: MojangApi,
    private val downloadRepository: DownloadRepository,
    private val gameDir: File
) {
    private val versionsDir = File(gameDir, "versions")
    private var cachedManifest: VersionManifest? = null

    fun getVersionManifest(forceRefresh: Boolean = false): Flow<Result<VersionManifest>> = flow {
        try {
            if (!forceRefresh && cachedManifest != null) {
                emit(Result.success(cachedManifest!!))
                return@flow
            }

            if (!NetworkUtils.isNetworkAvailable()) {
                emit(Result.failure(Exception("No internet connection")))
                return@flow
            }

            val manifest = mojangApi.getVersionManifest()
            cachedManifest = manifest
            emit(Result.success(manifest))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getVersionDetail(versionInfo: VersionInfo): Result<VersionDetail> = 
        withContext(Dispatchers.IO) {
            try {
                val localFile = File(versionsDir, "${versionInfo.id}/${versionInfo.id}.json")
                
                if (localFile.exists()) {
                    val json = localFile.readText()
                    val detail = com.google.gson.Gson().fromJson(json, VersionDetail::class.java)
                    if (detail != null) return@withContext Result.success(detail)
                }

                val detail = mojangApi.getVersionDetail(versionInfo.url)
                localFile.parentFile?.mkdirs()
                localFile.writeText(com.google.gson.Gson().toJson(detail))
                Result.success(detail)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun isVersionInstalled(versionId: String): Boolean {
        val versionDir = File(versionsDir, versionId)
        val jarFile = File(versionDir, "$versionId.jar")
        val jsonFile = File(versionDir, "$versionId.json")
        return jarFile.exists() && jsonFile.exists()
    }

    fun getInstalledVersions(): List<String> {
        return versionsDir.listFiles()?.filter { it.isDirectory }?.map { it.name } ?: emptyList()
    }

    suspend fun deleteVersion(versionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val versionDir = File(versionsDir, versionId)
            if (versionDir.exists()) {
                versionDir.deleteRecursively()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getVersionDirectory(versionId: String): File {
        return File(versionsDir, versionId)
    }
}
