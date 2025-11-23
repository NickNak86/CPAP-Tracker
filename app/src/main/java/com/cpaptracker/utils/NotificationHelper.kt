package com.cpaptracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.cpaptracker.MainActivity
import com.cpaptracker.R

object NotificationHelper {
    private const val TAG = "NotificationHelper"
    private const val CHANNEL_ID = "part_replacement_channel"
    private const val CHANNEL_NAME = "Part Replacement Reminders"
    private const val CHANNEL_DESCRIPTION = "Notifications for upcoming part replacements"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $CHANNEL_ID")
        }
    }

    /**
     * Check if the app has permission to post notifications
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Before Android 13, notification permission is granted by default
            true
        }
    }

    fun sendPartReplacementNotification(
        context: Context,
        partName: String,
        daysUntilReplacement: Long,
        notificationId: Int
    ) {
        // Check permission first
        if (!hasNotificationPermission(context)) {
            Log.w(TAG, "Cannot send notification - permission not granted")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            notificationId, // Use unique request code per notification
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
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

        val priority = when {
            daysUntilReplacement < 0 -> NotificationCompat.PRIORITY_HIGH
            daysUntilReplacement == 0L -> NotificationCompat.PRIORITY_HIGH
            else -> NotificationCompat.PRIORITY_DEFAULT
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
            Log.d(TAG, "Notification sent: $title (ID: $notificationId)")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when posting notification - permission may have been revoked", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send notification", e)
        }
    }
}
