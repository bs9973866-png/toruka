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
class HomeViewModel @Inject constructor(
    private val versionRepository: VersionRepository,
    private val profileRepository: ProfileRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _launchState = MutableStateFlow<LaunchState>(LaunchState.Idle)
    val launchState: StateFlow<LaunchState> = _launchState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val manifestResult = versionRepository.getVersionManifest().first()
                val selectedAccount = accountRepository.getSelectedAccount()
                val profiles = profileRepository.getAllProfiles().first()

                manifestResult.onSuccess { manifest ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            versionManifest = manifest,
                            selectedAccount = selectedAccount,
                            profiles = profiles,
                            latestRelease = manifest.latest.release,
                            latestSnapshot = manifest.latest.snapshot
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectProfile(profile: Profile) {
        _uiState.update { it.copy(selectedProfile = profile) }
    }

    fun launchGame() {
        val profile = _uiState.value.selectedProfile ?: return
        val account = _uiState.value.selectedAccount

        if (account == null) {
            _launchState.value = LaunchState.Error("No account selected")
            return
        }

        if (!versionRepository.isVersionInstalled(profile.versionId)) {
            _launchState.value = LaunchState.Error("Version not installed")
            return
        }

        viewModelScope.launch {
            _launchState.value = LaunchState.Preparing("Preparing to launch...")
            
            try {
                profileRepository.updateLastPlayed(profile.id)
                profileRepository.incrementPlayCount(profile.id)
                
                _launchState.value = LaunchState.Launching
                // Actual launch happens in GameActivity via Intent
            } catch (e: Exception) {
                _launchState.value = LaunchState.Error(e.message ?: "Launch failed")
            }
        }
    }

    fun resetLaunchState() {
        _launchState.value = LaunchState.Idle
    }
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val versionManifest: VersionManifest? = null,
    val selectedProfile: Profile? = null,
    val selectedAccount: Account? = null,
    val profiles: List<Profile> = emptyList(),
    val latestRelease: String = "",
    val latestSnapshot: String = ""
)
