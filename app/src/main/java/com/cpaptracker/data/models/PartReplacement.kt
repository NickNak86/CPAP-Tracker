package com.cpaptracker.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "part_replacements",
    foreignKeys = [
        ForeignKey(
            entity = Part::class,
            parentColumns = ["id"],
            childColumns = ["partId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PartReplacement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partId: Long,
    val lastReplacedDate: LocalDate,
    val nextReplacementDate: LocalDate,
    val isOrdered: Boolean = false,
    val orderDate: LocalDate? = null,
    val orderNotes: String = "",
    val replacementNotes: String = ""
)

data class PartWithReplacement(
    val part: Part,
    val replacement: PartReplacement?
) {
    val daysUntilReplacement: Long?
        get() = replacement?.let {
            java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), it.nextReplacementDate)
        }

    val isOverdue: Boolean
        get() = (daysUntilReplacement ?: 0) < 0

    val status: ReplacementStatus
        get() = when {
            replacement == null -> ReplacementStatus.NOT_TRACKED
            isOverdue -> ReplacementStatus.OVERDUE
            (daysUntilReplacement ?: 0) <= 7 -> ReplacementStatus.DUE_SOON
            replacement.isOrdered -> ReplacementStatus.ORDERED
            else -> ReplacementStatus.OK
        }
}

enum class ReplacementStatus {
    OK,
    DUE_SOON,
    OVERDUE,
    ORDERED,
    NOT_TRACKED
}
