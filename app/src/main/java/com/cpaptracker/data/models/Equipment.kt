package com.cpaptracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: EquipmentType,
    val manufacturer: String,
    val model: String,
    val serialNumber: String,
    val purchaseDate: LocalDate,
    val notes: String = ""
)

enum class EquipmentType {
    BIPAP_MACHINE,
    CPAP_MACHINE,
    MASK,
    HUMIDIFIER
}
