package com.cpaptracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cpaptracker.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [Equipment::class, Part::class, PartReplacement::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao
    abstract fun partDao(): PartDao
    abstract fun partReplacementDao(): PartReplacementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cpap_tracker_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val equipmentDao = database.equipmentDao()
            val partDao = database.partDao()

            // Add the user's BiPAP machine
            val machineId = equipmentDao.insertEquipment(
                Equipment(
                    type = EquipmentType.BIPAP_MACHINE,
                    manufacturer = "ResMed",
                    model = "AirCurve 10 VAuto",
                    serialNumber = "23233592809",
                    purchaseDate = LocalDate.now(),
                    notes = "BiPAP machine from VA"
                )
            )

            // Add the user's mask
            val maskId = equipmentDao.insertEquipment(
                Equipment(
                    type = EquipmentType.MASK,
                    manufacturer = "ResMed",
                    model = "AirFit F40",
                    purchaseDate = LocalDate.now(),
                    notes = "Full face mask"
                )
            )

            // Add parts for ResMed AirFit F40 mask
            val maskParts = listOf(
                Part(
                    name = "Mask Cushion",
                    type = PartType.MASK_CUSHION,
                    manufacturer = "ResMed",
                    compatibleModel = "AirFit F40",
                    recommendedReplacementDays = ReplacementSchedule.MASK_CUSHION_DAYS,
                    description = "Silicone cushion for full face mask"
                ),
                Part(
                    name = "Mask Frame",
                    type = PartType.MASK_FRAME,
                    manufacturer = "ResMed",
                    compatibleModel = "AirFit F40",
                    recommendedReplacementDays = ReplacementSchedule.MASK_FRAME_DAYS,
                    description = "Mask frame assembly"
                ),
                Part(
                    name = "Headgear",
                    type = PartType.HEADGEAR,
                    manufacturer = "ResMed",
                    compatibleModel = "AirFit F40",
                    recommendedReplacementDays = ReplacementSchedule.HEADGEAR_DAYS,
                    description = "Headgear straps"
                )
            )

            // Add parts for ResMed AirCurve 10 VAuto
            val machineParts = listOf(
                Part(
                    name = "Air Filter (Disposable)",
                    type = PartType.AIR_FILTER,
                    manufacturer = "ResMed",
                    compatibleModel = "AirCurve 10 VAuto",
                    recommendedReplacementDays = ReplacementSchedule.AIR_FILTER_DAYS,
                    description = "Disposable air filter - replace monthly"
                ),
                Part(
                    name = "Water Chamber",
                    type = PartType.WATER_CHAMBER,
                    manufacturer = "ResMed",
                    compatibleModel = "AirCurve 10 VAuto",
                    recommendedReplacementDays = ReplacementSchedule.WATER_CHAMBER_DAYS,
                    description = "Humidifier water chamber"
                ),
                Part(
                    name = "Standard Tubing",
                    type = PartType.TUBING,
                    manufacturer = "ResMed",
                    compatibleModel = "AirCurve 10 VAuto",
                    recommendedReplacementDays = ReplacementSchedule.TUBING_DAYS,
                    description = "6ft standard tubing"
                ),
                Part(
                    name = "Elbow Connector",
                    type = PartType.ELBOW_CONNECTOR,
                    manufacturer = "ResMed",
                    compatibleModel = "AirCurve 10 VAuto",
                    recommendedReplacementDays = ReplacementSchedule.ELBOW_CONNECTOR_DAYS,
                    description = "Swivel elbow connector"
                )
            )

            partDao.insertParts(maskParts + machineParts)
        }
    }
}
