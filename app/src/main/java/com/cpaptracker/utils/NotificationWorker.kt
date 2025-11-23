package com.cpaptracker.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cpaptracker.data.database.AppDatabase
import com.cpaptracker.data.repository.CPAPRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first

class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "NotificationWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting notification check...")

        return try {
            // Use a dedicated scope for database operations
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            val database = AppDatabase.getDatabase(context, scope)
            val repository = CPAPRepository(database)

            // Get all parts with upcoming replacements (within 30 days)
            val upcomingReplacements = repository.getUpcomingReplacements().first()

            Log.d(TAG, "Found ${upcomingReplacements.size} parts with upcoming replacements")

            var notificationsSent = 0
            upcomingReplacements.forEachIndexed { index, partWithReplacement ->
                partWithReplacement.daysUntilReplacement?.let { days ->
                    // Send notification if within 7 days or overdue
                    if (days <= 7) {
                        Log.d(TAG, "Sending notification for ${partWithReplacement.part.name} (${days} days)")
                        NotificationHelper.sendPartReplacementNotification(
                            context = context,
                            partName = partWithReplacement.part.name,
                            daysUntilReplacement = days,
                            notificationId = 1000 + index
                        )
                        notificationsSent++
                    }
                }
            }

            Log.d(TAG, "Notification check complete. Sent $notificationsSent notifications.")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during notification check", e)
            Result.retry()
        }
    }
}
