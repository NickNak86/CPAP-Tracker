package com.cpaptracker.data.database

import androidx.room.*
import com.cpaptracker.data.models.Part
import com.cpaptracker.data.models.PartReplacement
import com.cpaptracker.data.models.PartWithReplacement
import kotlinx.coroutines.flow.Flow

@Dao
interface PartReplacementDao {
    @Query("SELECT * FROM part_replacements ORDER BY nextReplacementDate ASC")
    fun getAllReplacements(): Flow<List<PartReplacement>>

    @Query("SELECT * FROM part_replacements WHERE partId = :partId ORDER BY lastReplacedDate DESC LIMIT 1")
    suspend fun getLatestReplacementForPart(partId: Long): PartReplacement?

    @Query("""
        SELECT * FROM parts
        LEFT JOIN part_replacements ON parts.id = part_replacements.partId
        WHERE part_replacements.id IN (
            SELECT MAX(id) FROM part_replacements GROUP BY partId
        ) OR part_replacements.id IS NULL
        ORDER BY nextReplacementDate ASC
    """)
    fun getPartsWithReplacements(): Flow<List<Map<String, Any>>>

    @Query("""
        SELECT parts.*, part_replacements.*
        FROM parts
        LEFT JOIN (
            SELECT * FROM part_replacements pr1
            WHERE lastReplacedDate = (
                SELECT MAX(lastReplacedDate) FROM part_replacements pr2
                WHERE pr2.partId = pr1.partId
            )
        ) as part_replacements ON parts.id = part_replacements.partId
        ORDER BY CASE
            WHEN part_replacements.nextReplacementDate IS NULL THEN 1
            ELSE 0
        END, part_replacements.nextReplacementDate ASC
    """)
    fun getAllPartsWithLatestReplacement(): Flow<Map<String, Any>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplacement(replacement: PartReplacement): Long

    @Update
    suspend fun updateReplacement(replacement: PartReplacement)

    @Delete
    suspend fun deleteReplacement(replacement: PartReplacement)

    @Query("DELETE FROM part_replacements WHERE partId = :partId")
    suspend fun deleteReplacementsForPart(partId: Long)
}
