package com.example.consecutivepractice.repositories

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.consecutivepractice.models.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ProfileRepository(private val application: Application) {

    private val sharedPreferences =
        application.getSharedPreferences(PROFILE_PREFERENCES, Context.MODE_PRIVATE)

    private val _profileData = MutableStateFlow(loadProfile())
    val profileData: StateFlow<UserProfile> = _profileData.asStateFlow()


    private fun loadProfile(): UserProfile {
        return UserProfile(
            fullName = sharedPreferences.getString(KEY_FULL_NAME, "") ?: "",
            avatarUri = sharedPreferences.getString(KEY_AVATAR_URI, "") ?: "",
            resumeUrl = sharedPreferences.getString(KEY_RESUME_URL, "") ?: "",
            jobTitle = sharedPreferences.getString(KEY_JOB_TITLE, "") ?: ""
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().apply {
                putString(KEY_FULL_NAME, profile.fullName)
                putString(KEY_AVATAR_URI, profile.avatarUri)
                putString(KEY_RESUME_URL, profile.resumeUrl)
                putString(KEY_JOB_TITLE, profile.jobTitle)
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
                val outputDir = application.getExternalFilesDir(null)
                val outputFile = File(outputDir, fileName)

                URL(url).openStream().use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }

                FileProvider.getUriForFile(
                    application.applicationContext,
                    "${application.packageName}.fileprovider",
                    outputFile
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        }
    }

    companion object {
        private const val PROFILE_PREFERENCES = "profile_preferences"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_AVATAR_URI = "avatar_uri"
        private const val KEY_RESUME_URL = "resume_url"
        private const val KEY_JOB_TITLE = "job_title"
    }
}

