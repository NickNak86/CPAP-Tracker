package com.cpaptracker.data.repository

import com.cpaptracker.data.database.AppDatabase
import com.cpaptracker.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class CPAPRepository(private val database: AppDatabase) {

    // Equipment operations
    fun getAllEquipment(): Flow<List<Equipment>> = database.equipmentDao().getAllEquipment()

    suspend fun getEquipmentById(id: Long): Equipment? = database.equipmentDao().getEquipmentById(id)

    suspend fun insertEquipment(equipment: Equipment): Long = database.equipmentDao().insertEquipment(equipment)

    suspend fun updateEquipment(equipment: Equipment) = database.equipmentDao().updateEquipment(equipment)

    suspend fun deleteEquipment(equipment: Equipment) = database.equipmentDao().deleteEquipment(equipment)

    // Part operations
    fun getAllParts(): Flow<List<Part>> = database.partDao().getAllParts()

    suspend fun getPartById(id: Long): Part? = database.partDao().getPartById(id)

    fun getPartsByModel(model: String): Flow<List<Part>> = database.partDao().getPartsByModel(model)

    suspend fun insertPart(part: Part): Long = database.partDao().insertPart(part)

    suspend fun updatePart(part: Part) = database.partDao().updatePart(part)

    suspend fun deletePart(part: Part) = database.partDao().deletePart(part)

    // Part replacement operations
    fun getAllReplacements(): Flow<List<PartReplacement>> = database.partReplacementDao().getAllReplacements()

    suspend fun getLatestReplacementForPart(partId: Long): PartReplacement? =
        database.partReplacementDao().getLatestReplacementForPart(partId)

    suspend fun insertReplacement(replacement: PartReplacement): Long =
        database.partReplacementDao().insertReplacement(replacement)

    suspend fun updateReplacement(replacement: PartReplacement) =
        database.partReplacementDao().updateReplacement(replacement)

    suspend fun deleteReplacement(replacement: PartReplacement) =
        database.partReplacementDao().deleteReplacement(replacement)

    // Combined operations
    fun getPartsWithReplacements(): Flow<List<PartWithReplacement>> {
        return combine(
            database.partDao().getAllParts(),
            database.partReplacementDao().getAllReplacements()
        ) { parts, replacements ->
            parts.map { part ->
                val latestReplacement = replacements
                    .filter { it.partId == part.id }
                    .maxByOrNull { it.lastReplacedDate }
                PartWithReplacement(part, latestReplacement)
            }.sortedBy { it.daysUntilReplacement }
        }
    }

    fun getUpcomingReplacements(): Flow<List<PartWithReplacement>> {
        return combine(
            database.partDao().getAllParts(),
            database.partReplacementDao().getAllReplacements()
        ) { parts, replacements ->
            parts.mapNotNull { part ->
                val latestReplacement = replacements
                    .filter { it.partId == part.id }
                    .maxByOrNull { it.lastReplacedDate }

                latestReplacement?.let {
                    PartWithReplacement(part, it)
                }
            }
            .filter { it.daysUntilReplacement != null && it.daysUntilReplacement!! <= 30 }
            .sortedBy { it.daysUntilReplacement }
        }
    }

    suspend fun markPartAsReplaced(partId: Long, replacementDate: LocalDate = LocalDate.now(), notes: String = "") {
        val part = getPartById(partId) ?: return
        val nextReplacementDate = replacementDate.plusDays(part.recommendedReplacementDays.toLong())

        val replacement = PartReplacement(
            partId = partId,
            lastReplacedDate = replacementDate,
            nextReplacementDate = nextReplacementDate,
            replacementNotes = notes
        )

        insertReplacement(replacement)
    }

    suspend fun markPartAsOrdered(partId: Long, orderDate: LocalDate = LocalDate.now(), notes: String = "") {
        val latestReplacement = getLatestReplacementForPart(partId) ?: return

        val updatedReplacement = latestReplacement.copy(
            isOrdered = true,
            orderDate = orderDate,
            orderNotes = notes
        )

        updateReplacement(updatedReplacement)
    }

    suspend fun initializePartSchedule(partId: Long, startDate: LocalDate = LocalDate.now()) {
        val part = getPartById(partId) ?: return
        val existingReplacement = getLatestReplacementForPart(partId)

        if (existingReplacement == null) {
            markPartAsReplaced(partId, startDate, "Initial setup")
        }
    }

    suspend fun initializeAllParts() {
        val parts = database.partDao().getAllParts()
        parts.collect { partList ->
            partList.forEach { part ->
                initializePartSchedule(part.id)
            }
        }
    }
}
