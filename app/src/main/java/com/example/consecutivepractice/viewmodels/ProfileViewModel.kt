package com.example.consecutivepractice.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractice.models.UserProfile
import com.example.consecutivepractice.repositories.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(application)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            repository.profileData.collect { profile ->
                _uiState.update {
                    it.copy(
                        profile = profile,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun downloadAndOpenResume() {
        val url = _uiState.value.profile.resumeUrl
        if (url.isNotBlank()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isDownloading = true) }
                val fileUri = repository.downloadResume(url)
                _uiState.update {
                    it.copy(
                        resumeFileUri = fileUri,
                        isDownloading = false,
                        errorMessage = if (fileUri == null) "Не удалось скачать резюме" else null
                    )
                }
            }
        } else {
            _uiState.update { it.copy(errorMessage = "URL резюме не указан") }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearResumeFile() {
        _uiState.update { it.copy(resumeFileUri = null) }
    }

    fun refreshProfile() {
        viewModelScope.launch {
            val profile = repository.getProfile()
            _uiState.update {
                it.copy(
                    profile = profile,
                    isLoading = false
                )
            }
        }
    }

}

data class ProfileUiState(
    val profile: UserProfile = UserProfile(),
    val isLoading: Boolean = true,
    val isDownloading: Boolean = false,
    val errorMessage: String? = null,
    val resumeFileUri: Uri? = null
)