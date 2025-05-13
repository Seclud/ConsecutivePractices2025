package com.example.profile.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profile.model.UserProfile
import com.example.profile.notifications.NotificationHelper
import com.example.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileEditViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun checkAllPermissions(context: Context) {
        // Проверка разрешения для камеры
        val cameraPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        updateCameraPermission(cameraPermissionGranted)

        // Определение и проверка разрешения на хранилище
        val storagePermission = if (Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val storagePermissionGranted = ContextCompat.checkSelfPermission(
            context,
            storagePermission
        ) == PackageManager.PERMISSION_GRANTED
        updateStoragePermission(storagePermissionGranted)

        // Проверка разрешения на уведомления
        val notificationPermissionGranted = if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        updateNotificationPermission(notificationPermissionGranted)

        // Проверка разрешения на уведомления по времени
        val exactAlarmPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.hasExactAlarmPermission()
        } else {
            true
        }
        updateExactAlarmPermission(exactAlarmPermissionGranted)
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
                        favoriteClassTime = profile.favoriteClassTime,
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


    fun updateCameraPermission(granted: Boolean) {
        _uiState.update { it.copy(cameraPermissionGranted = granted) }
    }

    fun updateStoragePermission(granted: Boolean) {
        _uiState.update { it.copy(storagePermissionGranted = granted) }
    }

    fun updateNotificationPermission(granted: Boolean) {
        _uiState.update { it.copy(notificationPermissionGranted = granted) }
    }

    fun updateExactAlarmPermission(granted: Boolean) {
        _uiState.update { it.copy(exactAlarmPermissionGranted = granted) }
    }

    fun setTempCameraUri(uri: String?) {
        _uiState.update { it.copy(tempCameraUri = uri) }
    }

    fun showPermissionDeniedDialog() {
        _uiState.update { it.copy(showPermissionDeniedDialog = true) }
    }

    fun hidePermissionDeniedDialog() {
        _uiState.update { it.copy(showPermissionDeniedDialog = false) }
    }

    fun showAlarmPermissionDialog() {
        _uiState.update { it.copy(showAlarmPermissionDialog = true) }
    }

    fun hideAlarmPermissionDialog() {
        _uiState.update { it.copy(showAlarmPermissionDialog = false) }
    }

    fun showImageSourceDialog() {
        _uiState.update { it.copy(showImageSourceDialog = true) }
    }

    fun hideImageSourceDialog() {
        _uiState.update { it.copy(showImageSourceDialog = false) }
    }

    fun showTimePicker() {
        _uiState.update { it.copy(showTimePicker = true) }
    }

    fun hideTimePicker() {
        _uiState.update { it.copy(showTimePicker = false) }
    }

    fun onFavoriteClassTimeChanged(newTime: String) {
        val timeError = validateTimeFormat(newTime)
        _uiState.update {
            it.copy(
                favoriteClassTime = newTime,
                favoriteClassTimeError = timeError
            )
        }
    }

    private fun validateTimeFormat(time: String): String? {
        if (time.isBlank()) return null

        val timeRegex = Regex("^([01]?[0-9]|2[0-3]):([0-5][0-9])$")
        return if (timeRegex.matches(time)) null else "Неверный формат времени. Используйте HH:MM"
    }

    fun setTimeFromPicker(hour: Int, minute: Int) {
        val timeString = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
        onFavoriteClassTimeChanged(timeString)
        hideTimePicker()
    }

    fun saveProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value

            val timeError = validateTimeFormat(currentState.favoriteClassTime)
            if (timeError != null) {
                _uiState.update { it.copy(favoriteClassTimeError = timeError) }
                return@launch
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                currentState.favoriteClassTime.isNotBlank() &&
                !currentState.exactAlarmPermissionGranted
            ) {
                _uiState.update { it.copy(showAlarmPermissionDialog = true) }
                return@launch
            }

            val updatedProfile = UserProfile(
                fullName = currentState.fullName,
                jobTitle = currentState.jobTitle,
                resumeUrl = currentState.resumeUrl,
                avatarUri = currentState.avatarUri,
                favoriteClassTime = currentState.favoriteClassTime
            )

            _uiState.update { it.copy(isSaving = true) }

            repository.saveProfile(updatedProfile)

            if (currentState.favoriteClassTime.isNotBlank()) {
                repository.scheduleClassNotification(
                    currentState.fullName,
                    currentState.favoriteClassTime
                )
            }

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
    val favoriteClassTime: String = "",
    val favoriteClassTimeError: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    val showImageSourceDialog: Boolean = false,
    val showPermissionDeniedDialog: Boolean = false,
    val showAlarmPermissionDialog: Boolean = false,
    val showTimePicker: Boolean = false,
    val tempCameraUri: String? = null,

    val cameraPermissionGranted: Boolean = false,
    val storagePermissionGranted: Boolean = false,
    val notificationPermissionGranted: Boolean = false,
    val exactAlarmPermissionGranted: Boolean = false
)