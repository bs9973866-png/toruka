package com.toruka.launcher.util

import java.io.*
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object FileUtils {

    fun ensureDirectory(dir: File): File {
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun deleteDirectory(dir: File): Boolean {
        if (!dir.exists()) return true
        
        val files = dir.listFiles()
        files?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete()
            }
        }
        return dir.delete()
    }

    fun calculateSha1(file: File): String {
        val digest = MessageDigest.getInstance("SHA-1")
        file.inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun extractZip(zipFile: File, destination: File) {
        ZipInputStream(zipFile.inputStream().buffered()).use { zis ->
            var entry: ZipEntry?
            while (zis.nextEntry.also { entry = it } != null) {
                entry?.let { zipEntry ->
                    val outFile = File(destination, zipEntry.name)
                    if (zipEntry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        outFile.outputStream().use { output ->
                            zis.copyTo(output)
                        }
                    }
                }
            }
        }
    }

    fun formatSize(size: Long): String {
        return when {
            size >= 1024 * 1024 * 1024 -> String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0))
            size >= 1024 * 1024 -> String.format("%.2f MB", size / (1024.0 * 1024.0))
            size >= 1024 -> String.format("%.2f KB", size / 1024.0)
            else -> "$size B"
        }
    }

    fun getFreeSpace(path: File): Long {
        return path.usableSpace
    }

    fun copyFile(src: File, dst: File) {
        src.inputStream().use { input ->
            dst.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}
