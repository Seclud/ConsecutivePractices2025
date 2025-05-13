package com.example.profile.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.profile.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClassNotificationReceiver : BroadcastReceiver(), KoinComponent {
    private val activityProvider: ActivityProvider by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val username = intent.getStringExtra(NotificationHelper.EXTRA_USERNAME)
        val classTime = intent.getStringExtra(NotificationHelper.EXTRA_CLASS_TIME)

        Log.d(
            "ClassNotificationReceiver",
            "Notification triggered for $username at time $classTime"
        )

        val notificationIntent = activityProvider.getMainActivityIntent(context)

        val pendingIntentFlags =
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            pendingIntentFlags
        )
        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(
                context.getString(
                    R.string.notification_class_text,
                    username,
                    classTime
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NotificationHelper.NOTIFICATION_ID, notification)

        if (username != null) {
            if (classTime != null) {
                NotificationHelper(context).scheduleClassNotification(username, classTime)
            }
        }
    }
}
