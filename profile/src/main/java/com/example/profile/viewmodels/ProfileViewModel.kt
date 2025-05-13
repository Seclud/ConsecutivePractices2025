package com.example.profile.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profile.model.UserProfile
import com.example.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        refreshProfile()
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
                        shouldOpenPdf = fileUri != null,
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
        _uiState.update { it.copy(resumeFileUri = null, shouldOpenPdf = false) }
    }

    fun onPdfOpenFailure() {
        _uiState.update {
            it.copy(
                errorMessage = "Не найдено приложение для просмотра PDF",
                resumeFileUri = null,
                shouldOpenPdf = false
            )
        }
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
    val resumeFileUri: Uri? = null,
    val shouldOpenPdf: Boolean = false
)