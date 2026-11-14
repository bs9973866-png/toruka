package com.toruka.launcher.data.model

import com.google.gson.annotations.SerializedName

/**
 * Detailed version metadata from Mojang
 */
data class VersionDetail(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("mainClass")
    val mainClass: String,
    @SerializedName("assets")
    val assets: String,
    @SerializedName("assetIndex")
    val assetIndex: AssetIndex,
    @SerializedName("downloads")
    val downloads: Downloads,
    @SerializedName("libraries")
    val libraries: List<Library>,
    @SerializedName("logging")
    val logging: Logging? = null,
    @SerializedName("minecraftArguments")
    val minecraftArguments: String? = null,
    @SerializedName("arguments")
    val arguments: Arguments? = null,
    @SerializedName("javaVersion")
    val javaVersion: JavaVersion? = null,
    @SerializedName("complianceLevel")
    val complianceLevel: Int = 0
) {
    fun getJvmArguments(): List<String> {
        return arguments?.jvm?.mapNotNull { arg ->
            when {
                arg is String -> arg
                arg is Map<*, *> -> null
                else -> null
            }
        } ?: listOf(
            "-Djava.library.path=\${natives_directory}",
            "-cp", "\${classpath}"
        )
    }

    fun getGameArguments(): List<String> {
        val args = mutableListOf<String>()
        
        minecraftArguments?.let { legacyArgs ->
            args.addAll(legacyArgs.split(" "))
            return args
        }
        
        arguments?.game?.forEach { arg ->
            when (arg) {
                is String -> args.add(arg)
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    val rules = arg["rules"] as? List<Map<String, Any>>
                    val value = arg["value"]
                    if (rules != null && value != null) {
                        if (evaluateRules(rules)) {
                            when (value) {
                                is String -> args.add(value)
                                is List<*> -> args.addAll(value.filterIsInstance<String>())
                            }
                        }
                    }
                }
            }
        }
        
        return args
    }

    @Suppress("UNCHECKED_CAST")
    private fun evaluateRules(rules: List<Map<String, Any>>): Boolean {
        for (rule in rules) {
            val action = rule["action"] as? String ?: continue
            val os = rule["os"] as? Map<String, String>
            
            if (os != null) {
                val osName = System.getProperty("os.name")?.lowercase() ?: ""
                val ruleName = os["name"]?.lowercase() ?: ""
                if (!osName.contains(ruleName)) {
                    return action == "disallow"
                }
            }
            
            return action == "allow"
        }
        return true
    }
}

data class AssetIndex(
    @SerializedName("id")
    val id: String,
    @SerializedName("sha1")
    val sha1: String,
    @SerializedName("size")
    val size: Long,
    @SerializedName("totalSize")
    val totalSize: Long,
    @SerializedName("url")
    val url: String
)

data class Downloads(
    @SerializedName("client")
    val client: DownloadInfo,
    @SerializedName("client_mappings")
    val clientMappings: DownloadInfo? = null,
    @SerializedName("server")
    val server: DownloadInfo? = null,
    @SerializedName("server_mappings")
    val serverMappings: DownloadInfo? = null
)

data class DownloadInfo(
    @SerializedName("sha1")
    val sha1: String,
    @SerializedName("size")
    val size: Long,
    @SerializedName("url")
    val url: String
)

data class Library(
    @SerializedName("downloads")
    val downloads: LibraryDownloads,
    @SerializedName("name")
    val name: String,
    @SerializedName("rules")
    val rules: List<Map<String, Any>>? = null,
    @SerializedName("natives")
    val natives: Map<String, String>? = null,
    @SerializedName("extract")
    val extract: ExtractRules? = null
) {
    fun isAllowed(): Boolean {
        rules?.let { ruleList ->
            for (rule in ruleList) {
                val action = rule["action"] as? String ?: continue
                val os = rule["os"] as? Map<String, String>
                if (os != null) {
                    val osName = System.getProperty("os.name")?.lowercase() ?: ""
                    val ruleName = os["name"]?.lowercase() ?: ""
                    if (!osName.contains(ruleName)) {
                        return action == "disallow"
                    }
                }
                return action == "allow"
            }
        }
        return true
    }
    
    fun getArtifactPath(): String {
        val parts = name.split(":")
        val group = parts[0].replace(".", "/")
        val artifact = parts[1]
        val version = parts[2]
        return "$group/$artifact/$version/$artifact-$version.jar"
    }
    
    fun getNativeClassifier(): String? {
        val osName = System.getProperty("os.name")?.lowercase() ?: ""
        return when {
            osName.contains("linux") -> natives?.get("linux")
            osName.contains("windows") -> natives?.get("windows")
            osName.contains("mac") -> natives?.get("osx")
            else -> null
        }
    }
}

data class LibraryDownloads(
    @SerializedName("artifact")
    val artifact: Artifact? = null,
    @SerializedName("classifiers")
    val classifiers: Map<String, Artifact>? = null
)

data class Artifact(
    @SerializedName("path")
    val path: String,
    @SerializedName("sha1")
    val sha1: String,
    @SerializedName("size")
    val size: Long,
    @SerializedName("url")
    val url: String
)

data class ExtractRules(
    @SerializedName("exclude")
    val exclude: List<String>? = null
)

data class Arguments(
    @SerializedName("game")
    val game: List<Any>,
    @SerializedName("jvm")
    val jvm: List<Any>
)

data class JavaVersion(
    @SerializedName("component")
    val component: String,
    @SerializedName("majorVersion")
    val majorVersion: Int
)

data class Logging(
    @SerializedName("client")
    val client: LogConfig
)

data class LogConfig(
    @SerializedName("argument")
    val argument: String,
    @SerializedName("file")
    val file: LogFile,
    @SerializedName("type")
    val type: String
)

data class LogFile(
    @SerializedName("id")
    val id: String,
    @SerializedName("sha1")
    val sha1: String,
    @SerializedName("size")
    val size: Long,
    @SerializedName("url")
    val url: String
)
