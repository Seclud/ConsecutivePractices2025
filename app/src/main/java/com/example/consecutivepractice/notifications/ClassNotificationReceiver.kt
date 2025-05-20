package com.example.consecutivepractice.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.consecutivepractice.MainActivity
import com.example.consecutivepractice.R

class ClassNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val username = intent.getStringExtra(NotificationHelper.EXTRA_USERNAME)
        val classTime = intent.getStringExtra(NotificationHelper.EXTRA_CLASS_TIME)

        Log.d(
            "ClassNotificationReceiver",
            "Notification triggered for $username at time $classTime"
        )

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntentFlags =
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            pendingIntentFlags
        )

        val contentText = "$username, ваша любимая пара в $classTime начинается"


        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Время любимой пары")
            .setContentText(contentText)
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
