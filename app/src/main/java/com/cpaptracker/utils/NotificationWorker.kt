package com.cpaptracker.utils

import android.content.Context
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

    override suspend fun doWork(): Result {
        return try {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            val database = AppDatabase.getDatabase(context, scope)
            val repository = CPAPRepository(database)

            // Get all parts with upcoming replacements (within 30 days)
            val upcomingReplacements = repository.getUpcomingReplacements().first()

            upcomingReplacements.forEachIndexed { index, partWithReplacement ->
                partWithReplacement.daysUntilReplacement?.let { days ->
                    // Send notification if within 7 days or overdue
                    if (days <= 7) {
                        NotificationHelper.sendPartReplacementNotification(
                            context = context,
                            partName = partWithReplacement.part.name,
                            daysUntilReplacement = days,
                            notificationId = 1000 + index
                        )
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
