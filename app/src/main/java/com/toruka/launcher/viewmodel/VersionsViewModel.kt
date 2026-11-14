package com.toruka.launcher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toruka.launcher.data.model.*
import com.toruka.launcher.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VersionsViewModel @Inject constructor(
    private val versionRepository: VersionRepository,
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VersionsUiState())
    val uiState: StateFlow<VersionsUiState> = _uiState.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    init {
        loadVersions()
    }

    fun loadVersions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            versionRepository.getVersionManifest().collect { result ->
                result.onSuccess { manifest ->
                    val installedVersions = versionRepository.getInstalledVersions()
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            versions = manifest.versions,
                            installedVersions = installedVersions,
                            latestRelease = manifest.latest.release,
                            latestSnapshot = manifest.latest.snapshot
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    fun installVersion(versionInfo: VersionInfo) {
        viewModelScope.launch {
            _downloadState.value = DownloadState.Downloading(0f, 0, 0)
            
            val detailResult = versionRepository.getVersionDetail(versionInfo)
            
            detailResult.onSuccess { detail ->
                downloadRepository.downloadVersion(versionInfo.id, detail).collect { state ->
                    _downloadState.value = state
                }
                
                // Refresh installed versions
                val installed = versionRepository.getInstalledVersions()
                _uiState.update { it.copy(installedVersions = installed) }
            }.onFailure { error ->
                _downloadState.value = DownloadState.Error(error.message ?: "Failed to get version details")
            }
        }
    }

    fun deleteVersion(versionId: String) {
        viewModelScope.launch {
            versionRepository.deleteVersion(versionId)
            val installed = versionRepository.getInstalledVersions()
            _uiState.update { it.copy(installedVersions = installed) }
        }
    }

    fun isVersionInstalled(versionId: String): Boolean {
        return versionRepository.isVersionInstalled(versionId)
    }

    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }

    fun filterVersions(type: VersionType?) {
        _uiState.update { it.copy(filterType = type) }
    }
}

data class VersionsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val versions: List<VersionInfo> = emptyList(),
    val installedVersions: List<String> = emptyList(),
    val latestRelease: String = "",
    val latestSnapshot: String = "",
    val filterType: VersionType? = null
) {
    val filteredVersions: List<VersionInfo>
        get() = if (filterType != null) {
            versions.filter { VersionType.fromString(it.type) == filterType }
        } else {
            versions
        }
}
