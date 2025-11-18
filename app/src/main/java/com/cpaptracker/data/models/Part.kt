package com.cpaptracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parts")
data class Part(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: PartType,
    val manufacturer: String,
    val compatibleModel: String,
    val recommendedReplacementDays: Int,
    val description: String = ""
)

enum class PartType {
    MASK_CUSHION,
    MASK_FRAME,
    HEADGEAR,
    AIR_FILTER,
    WATER_CHAMBER,
    TUBING,
    ELBOW_CONNECTOR,
    CHINSTRAP,
    HUMIDIFIER_FILTER
}

// Manufacturer recommended replacement schedules (in days)
object ReplacementSchedule {
    // ResMed AirFit F40 Mask Components
    const val MASK_CUSHION_DAYS = 30        // Monthly
    const val MASK_FRAME_DAYS = 180         // Every 6 months
    const val HEADGEAR_DAYS = 180           // Every 6 months

    // ResMed AirCurve 10 VAuto Machine Components
    const val AIR_FILTER_DAYS = 30          // Monthly (disposable)
    const val WATER_CHAMBER_DAYS = 180      // Every 6 months
    const val TUBING_DAYS = 90              // Every 3 months
    const val ELBOW_CONNECTOR_DAYS = 180    // Every 6 months
}
