package com.cpaptracker.data.models

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromEquipmentType(value: EquipmentType): String {
        return value.name
    }

    @TypeConverter
    fun toEquipmentType(value: String): EquipmentType {
        return EquipmentType.valueOf(value)
    }

    @TypeConverter
    fun fromPartType(value: PartType): String {
        return value.name
    }

    @TypeConverter
    fun toPartType(value: String): PartType {
        return PartType.valueOf(value)
    }
}
