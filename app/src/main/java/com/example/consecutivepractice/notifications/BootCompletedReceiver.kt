package com.example.consecutivepractice.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.consecutivepractice.repositories.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


class BootCompletedReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootCompletedReceiver", "Boot completed")

            val profileRepository: ProfileRepository by inject(ProfileRepository::class.java)

            scope.launch {
                try {
                    val profile = profileRepository.getProfile()
                    if (profile.favoriteClassTime.isNotBlank() && profile.fullName.isNotBlank()) {
                        profileRepository.scheduleClassNotification(
                            profile.fullName,
                            profile.favoriteClassTime
                        )
                    }
                } catch (e: Exception) {
                    Log.e("BootCompletedReceiver", "Error rescheduling notifications", e)
                }
            }
        }
    }
}
