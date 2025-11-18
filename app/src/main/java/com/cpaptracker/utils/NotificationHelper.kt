package com.cpaptracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cpaptracker.MainActivity
import com.cpaptracker.R

object NotificationHelper {
    private const val CHANNEL_ID = "part_replacement_channel"
    private const val CHANNEL_NAME = "Part Replacement Reminders"
    private const val CHANNEL_DESCRIPTION = "Notifications for upcoming part replacements"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendPartReplacementNotification(
        context: Context,
        partName: String,
        daysUntilReplacement: Long,
        notificationId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val title = when {
            daysUntilReplacement < 0 -> "OVERDUE: Replace $partName"
            daysUntilReplacement == 0L -> "Replace $partName Today"
            daysUntilReplacement <= 7 -> "Replace $partName Soon"
            else -> "Upcoming: Replace $partName"
        }

        val message = when {
            daysUntilReplacement < 0 -> "Your $partName is overdue for replacement by ${-daysUntilReplacement} days"
            daysUntilReplacement == 0L -> "Time to replace your $partName"
            daysUntilReplacement <= 7 -> "Replace your $partName in $daysUntilReplacement days"
            else -> "Replace your $partName in $daysUntilReplacement days"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Check notification permission on Android 13+
                if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    notify(notificationId, builder.build())
                }
            } else {
                notify(notificationId, builder.build())
            }
        }
    }
}
