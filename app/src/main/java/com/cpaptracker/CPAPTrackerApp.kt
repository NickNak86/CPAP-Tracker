package com.cpaptracker

import android.app.Application
import androidx.work.*
import com.cpaptracker.data.database.AppDatabase
import com.cpaptracker.data.repository.CPAPRepository
import com.cpaptracker.utils.NotificationHelper
import com.cpaptracker.utils.NotificationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CPAPTrackerApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { CPAPRepository(database) }

    override fun onCreate() {
        super.onCreate()

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        // Initialize parts with replacement schedules
        applicationScope.launch {
            repository.initializeAllParts()
        }

        // Schedule daily notification checks
        scheduleNotificationWorker()
    }

    private fun scheduleNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CPAPNotifications",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorkRequest
        )
    }
}
