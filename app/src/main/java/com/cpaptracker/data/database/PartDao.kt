package com.cpaptracker.data.database

import androidx.room.*
import com.cpaptracker.data.models.Part
import kotlinx.coroutines.flow.Flow

@Dao
interface PartDao {
    @Query("SELECT * FROM parts ORDER BY name ASC")
    fun getAllParts(): Flow<List<Part>>

    @Query("SELECT * FROM parts WHERE id = :id")
    suspend fun getPartById(id: Long): Part?

    @Query("SELECT * FROM parts WHERE compatibleModel = :model")
    fun getPartsByModel(model: String): Flow<List<Part>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPart(part: Part): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParts(parts: List<Part>)

    @Update
    suspend fun updatePart(part: Part)

    @Delete
    suspend fun deletePart(part: Part)
}
