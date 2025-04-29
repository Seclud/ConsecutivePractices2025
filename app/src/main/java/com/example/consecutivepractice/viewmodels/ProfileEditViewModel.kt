package com.example.consecutivepractice.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractice.models.UserProfile
import com.example.consecutivepractice.repositories.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileEditViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(application)

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.profileData.collect { profile ->
                _uiState.update {
                    it.copy(
                        fullName = profile.fullName,
                        jobTitle = profile.jobTitle,
                        resumeUrl = profile.resumeUrl,
                        avatarUri = profile.avatarUri,
                        isLoading = false
                    )
                }
            }
        }
    }


    fun onFullNameChanged(newName: String) {
        _uiState.update { it.copy(fullName = newName) }
    }


    fun onJobTitleChanged(newTitle: String) {
        _uiState.update { it.copy(jobTitle = newTitle) }
    }


    fun onResumeUrlChanged(newUrl: String) {
        _uiState.update { it.copy(resumeUrl = newUrl) }
    }


    fun onAvatarChanged(newUri: String) {
        _uiState.update { it.copy(avatarUri = newUri) }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value

            val updatedProfile = UserProfile(
                fullName = currentState.fullName,
                jobTitle = currentState.jobTitle,
                resumeUrl = currentState.resumeUrl,
                avatarUri = currentState.avatarUri
            )

            _uiState.update { it.copy(isSaving = true) }

            repository.saveProfile(updatedProfile)

            _uiState.update { it.copy(isSaving = false) }

            onSuccess()
        }
    }
}

data class ProfileEditUiState(
    val fullName: String = "",
    val jobTitle: String = "",
    val resumeUrl: String = "",
    val avatarUri: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false
)