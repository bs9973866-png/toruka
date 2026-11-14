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
class SettingsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun createProfile(
        name: String,
        versionId: String,
        memory: Int,
        renderer: RendererType,
        javaArgs: String,
        gameArgs: String
    ) {
        viewModelScope.launch {
            val profile = Profile(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                versionId = versionId,
                gameDir = ".minecraft",
                memoryAllocation = memory,
                renderer = renderer,
                javaArgs = javaArgs,
                gameArgs = gameArgs
            )
            profileRepository.createProfile(profile)
        }
    }

    fun updateProfile(profile: Profile) {
        viewModelScope.launch {
            profileRepository.updateProfile(profile)
        }
    }

    fun deleteProfile(profileId: String) {
        viewModelScope.launch {
            profileRepository.deleteProfileById(profileId)
        }
    }
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentProfile: Profile? = null
)
