package com.example.profile.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.profile.di.AndroidContextProvider
import com.example.profile.model.UserProfile
import com.example.profile.notifications.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class ProfileRepository(private val contextProvider: AndroidContextProvider) {

    private val sharedPreferences =
        contextProvider.getSharedPreferences(PROFILE_PREFERENCES, Context.MODE_PRIVATE)

    private val _profileData = MutableStateFlow(loadProfile())
    val profileData: StateFlow<UserProfile> = _profileData.asStateFlow()
    private fun loadProfile(): UserProfile {
        return UserProfile(
            fullName = sharedPreferences.getString(KEY_FULL_NAME, "") ?: "",
            avatarUri = sharedPreferences.getString(KEY_AVATAR_URI, "") ?: "",
            resumeUrl = sharedPreferences.getString(KEY_RESUME_URL, "") ?: "",
            jobTitle = sharedPreferences.getString(KEY_JOB_TITLE, "") ?: "",
            favoriteClassTime = sharedPreferences.getString(KEY_FAVORITE_CLASS_TIME, "") ?: ""
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putString(KEY_FULL_NAME, profile.fullName)
                putString(KEY_AVATAR_URI, profile.avatarUri)
                putString(KEY_RESUME_URL, profile.resumeUrl)
                putString(KEY_JOB_TITLE, profile.jobTitle)
                putString(KEY_FAVORITE_CLASS_TIME, profile.favoriteClassTime)
                apply()
            }

            _profileData.value = profile
        }
    }

    suspend fun getProfile(): UserProfile {
        return withContext(Dispatchers.IO) {
            loadProfile()
        }
    }

    suspend fun downloadResume(url: String): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "resume_${System.currentTimeMillis()}.pdf"
                val applicationContext = contextProvider.getApplicationContext()
                val outputDir = applicationContext.getExternalFilesDir(null)
                val outputFile = File(outputDir, fileName)

                URL(url).openStream().use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }

                FileProvider.getUriForFile(
                    applicationContext,
                    "${applicationContext.packageName}.fileprovider",
                    outputFile
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        }
    }

    fun scheduleClassNotification(fullName: String, classTime: String) {
        val context = contextProvider.getApplicationContext()
        val notificationHelper = NotificationHelper(context)
        notificationHelper.scheduleClassNotification(fullName, classTime)
    }

    companion object {
        private const val PROFILE_PREFERENCES = "profile_preferences"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_AVATAR_URI = "avatar_uri"
        private const val KEY_RESUME_URL = "resume_url"
        private const val KEY_JOB_TITLE = "job_title"
        private const val KEY_FAVORITE_CLASS_TIME = "favorite_class_time"
    }
}