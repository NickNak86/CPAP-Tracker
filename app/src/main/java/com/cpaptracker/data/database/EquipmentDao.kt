package com.cpaptracker.data.database

import androidx.room.*
import com.cpaptracker.data.models.Equipment
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM equipment ORDER BY purchaseDate DESC")
    fun getAllEquipment(): Flow<List<Equipment>>

    @Query("SELECT * FROM equipment WHERE id = :id")
    suspend fun getEquipmentById(id: Long): Equipment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: Equipment): Long

    @Update
    suspend fun updateEquipment(equipment: Equipment)

    @Delete
    suspend fun deleteEquipment(equipment: Equipment)
}
