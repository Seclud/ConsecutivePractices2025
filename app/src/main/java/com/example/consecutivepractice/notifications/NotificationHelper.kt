package com.example.consecutivepractice.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.consecutivepractice.MainActivity
import com.example.consecutivepractice.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationHelper(private val context: Context) {

    init {
        createNotificationChannel()
    }

    fun hasExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun getExactAlarmPermissionIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent().apply {
                action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            }
        } else {
            Intent(Settings.ACTION_SETTINGS)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Уведомления о парах"
            val descriptionText = "Канал уведомлений о парах"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleClassNotification(username: String, classTime: String) {
        try {
            if (classTime.isBlank()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasExactAlarmPermission()) {
                Log.e(
                    "NotificationHelper",
                    "No permission for exact alarms"
                )
                return
            }
            val (hours, minutes) = classTime.split(":").map { it.toInt() }

            val currentTime = Calendar.getInstance()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (get(Calendar.HOUR_OF_DAY) < currentTime.get(Calendar.HOUR_OF_DAY) ||
                    (get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY) &&
                            get(Calendar.MINUTE) <= currentTime.get(Calendar.MINUTE))
                ) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val timeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            Log.d(
                "NotificationHelper",
                "Current device time: ${timeFormat.format(currentTime.time)}"
            )
            Log.d(
                "NotificationHelper",
                "Setting notification for: ${timeFormat.format(calendar.time)}"
            )

            val intent = Intent(context, ClassNotificationReceiver::class.java).apply {
                putExtra(EXTRA_USERNAME, username)
                putExtra(EXTRA_CLASS_TIME, classTime)
            }

            cancelScheduledNotification()

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID,
                intent,
                pendingIntentFlags
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }

            Log.d(
                "NotificationHelper",
                "Notification successfully scheduled for: ${timeFormat.format(calendar.time)}"
            )
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error scheduling notification", e)
        }
    }

    fun cancelScheduledNotification() {
        val intent = Intent(context, ClassNotificationReceiver::class.java)

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            pendingIntentFlags
        )

        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("NotificationHelper", "Previous notification cancelled")
        }
    }

    fun showDebugNotification(username: String) {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            pendingIntentFlags
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Тестовое уведомление")
            .setContentText("$username, это тестовое уведомление!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "class_notifications"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_CLASS_TIME = "extra_class_time"
    }
}
