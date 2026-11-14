package com.toruka.launcher.data.repository

import com.toruka.launcher.data.api.DownloadApi
import com.toruka.launcher.data.api.MojangApi
import com.toruka.launcher.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    private val downloadApi: DownloadApi,
    private val gameDir: File
) {
    private val versionsDir = File(gameDir, "versions")
    private val librariesDir = File(gameDir, "libraries")
    private val assetsDir = File(gameDir, "assets")
    private val nativesDir = File(gameDir, "natives")

    suspend fun downloadVersion(
        versionId: String,
        versionDetail: VersionDetail
    ): Flow<DownloadState> = channelFlow {
        try {
            send(DownloadState.Downloading(0f, 0, 0))

            val versionDir = File(versionsDir, versionId)
            versionDir.mkdirs()

            // Download client JAR
            val clientJar = File(versionDir, "$versionId.jar")
            val clientUrl = versionDetail.downloads.client.url
            val clientSize = versionDetail.downloads.client.size
            
            downloadFileWithProgress(clientUrl, clientJar, clientSize) { progress, downloaded, total ->
                send(DownloadState.Downloading(progress * 0.3f, downloaded, total))
            }

            // Verify client JAR
            if (!verifySha1(clientJar, versionDetail.downloads.client.sha1)) {
                send(DownloadState.Error("Client JAR SHA1 mismatch"))
                return@channelFlow
            }

            send(DownloadState.Installing(0.3f))

            // Download libraries
            val libraries = versionDetail.libraries.filter { it.isAllowed() }
            val totalLibs = libraries.size
            
            libraries.forEachIndexed { index, library ->
                val libFile = File(librariesDir, library.getArtifactPath())
                libFile.parentFile?.mkdirs()
                
                library.downloads.artifact?.let { artifact ->
                    if (!libFile.exists() || !verifySha1(libFile, artifact.sha1)) {
                        downloadFile(artifact.url, libFile)
                    }
                }

                // Download native library if needed
                library.getNativeClassifier()?.let { classifier ->
                    library.downloads.classifiers?.get(classifier)?.let { nativeArtifact ->
                        val nativeFile = File(nativesDir, nativeArtifact.path)
                        nativeFile.parentFile?.mkdirs()
                        if (!nativeFile.exists() || !verifySha1(nativeFile, nativeArtifact.sha1)) {
                            downloadFile(nativeArtifact.url, nativeFile)
                        }
                    }
                }

                val libProgress = 0.3f + (index.toFloat() / totalLibs.toFloat()) * 0.4f
                send(DownloadState.Installing(libProgress))
            }

            send(DownloadState.Installing(0.7f))

            // Download asset index
            val assetIndexFile = File(assetsDir, "indexes/${versionDetail.assetIndex.id}.json")
            assetIndexFile.parentFile?.mkdirs()
            downloadFile(versionDetail.assetIndex.url, assetIndexFile)

            send(DownloadState.Installing(0.8f))

            // Parse and download assets
            val assetIndex = parseAssetIndex(assetIndexFile)
            val totalAssets = assetIndex.objects.size
            
            assetIndex.objects.entries.forEachIndexed { index, (name, obj) ->
                val hashPrefix = obj.hash.substring(0, 2)
                val assetFile = File(assetsDir, "objects/$hashPrefix/${obj.hash}")
                assetFile.parentFile?.mkdirs()
                
                if (!assetFile.exists() || !verifySha1(assetFile, obj.hash)) {
                    val assetUrl = "${MojangApi.ASSETS_URL}$hashPrefix/${obj.hash}"
                    downloadFile(assetUrl, assetFile)
                }

                val assetProgress = 0.8f + (index.toFloat() / totalAssets.toFloat()) * 0.2f
                send(DownloadState.Installing(assetProgress))
            }

            send(DownloadState.Completed)
        } catch (e: Exception) {
            send(DownloadState.Error(e.message ?: "Unknown download error"))
        }
    }

    private suspend fun downloadFile(url: String, destination: File) = withContext(Dispatchers.IO) {
        val response = downloadApi.downloadFile(url)
        destination.outputStream().use { output ->
            response.byteStream().use { input ->
                input.copyTo(output)
            }
        }
    }

    private suspend fun downloadFileWithProgress(
        url: String,
        destination: File,
        expectedSize: Long,
        onProgress: (Float, Long, Long) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = downloadApi.downloadFile(url)
        var totalBytesRead = 0L
        
        destination.outputStream().use { output ->
            response.byteStream().use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytesRead: Int
                
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    
                    if (expectedSize > 0) {
                        val progress = totalBytesRead.toFloat() / expectedSize.toFloat()
                        onProgress(progress.coerceIn(0f, 1f), totalBytesRead, expectedSize)
                    }
                }
            }
        }
    }

    private fun verifySha1(file: File, expectedSha1: String): Boolean {
        if (!file.exists()) return false
        val digest = MessageDigest.getInstance("SHA-1")
        file.inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        val sha1 = digest.digest().joinToString("") { "%02x".format(it) }
        return sha1.equals(expectedSha1, ignoreCase = true)
    }

    private fun parseAssetIndex(file: File): AssetIndexData {
        val gson = com.google.gson.Gson()
        return gson.fromJson(file.readText(), AssetIndexData::class.java)
    }

    data class AssetIndexData(
        val objects: Map<String, AssetObject>
    )

    data class AssetObject(
        val hash: String,
        val size: Long
    )
}
