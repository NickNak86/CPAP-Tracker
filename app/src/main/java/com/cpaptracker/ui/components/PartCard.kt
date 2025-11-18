package com.cpaptracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cpaptracker.data.models.PartWithReplacement
import com.cpaptracker.data.models.ReplacementStatus
import com.cpaptracker.ui.theme.*
import java.time.format.DateTimeFormatter

@Composable
fun PartCard(
    partWithReplacement: PartWithReplacement,
    onMarkReplaced: () -> Unit,
    onOrderPart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (partWithReplacement.status) {
                ReplacementStatus.OVERDUE -> ErrorRedLight.copy(alpha = 0.1f)
                ReplacementStatus.DUE_SOON -> WarningOrangeLight.copy(alpha = 0.1f)
                ReplacementStatus.ORDERED -> SecondaryGreenLight.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with part name and icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getPartIcon(partWithReplacement.part.type.name),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = partWithReplacement.part.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = partWithReplacement.part.compatibleModel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                StatusBadge(status = partWithReplacement.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Replacement info
            partWithReplacement.replacement?.let { replacement ->
                Column {
                    InfoRow(
                        label = "Last Replaced",
                        value = replacement.lastReplacedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                    )
                    InfoRow(
                        label = "Next Replacement",
                        value = replacement.nextReplacementDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                    )

                    partWithReplacement.daysUntilReplacement?.let { days ->
                        val daysText = when {
                            days < 0 -> "Overdue by ${-days} days"
                            days == 0L -> "Replace today"
                            else -> "$days days remaining"
                        }
                        InfoRow(label = "Status", value = daysText)
                    }

                    if (replacement.isOrdered) {
                        InfoRow(
                            label = "Ordered",
                            value = replacement.orderDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "Yes"
                        )
                    }
                }
            } ?: run {
                Text(
                    text = "Not yet tracking",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (partWithReplacement.replacement != null && !partWithReplacement.replacement.isOrdered) {
                    OutlinedButton(
                        onClick = onOrderPart,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Order")
                    }
                }

                Button(onClick = onMarkReplaced) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Replaced")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun StatusBadge(status: ReplacementStatus) {
    val (text, color) = when (status) {
        ReplacementStatus.OK -> "OK" to SecondaryGreen
        ReplacementStatus.DUE_SOON -> "Due Soon" to WarningOrange
        ReplacementStatus.OVERDUE -> "Overdue" to ErrorRed
        ReplacementStatus.ORDERED -> "Ordered" to PrimaryBlue
        ReplacementStatus.NOT_TRACKED -> "Not Tracked" to MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun getPartIcon(partType: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (partType) {
        "MASK_CUSHION", "MASK_FRAME" -> Icons.Default.Face
        "HEADGEAR" -> Icons.Default.Settings
        "AIR_FILTER" -> Icons.Default.Air
        "WATER_CHAMBER" -> Icons.Default.WaterDrop
        "TUBING" -> Icons.Default.Cable
        "ELBOW_CONNECTOR" -> Icons.Default.Extension
        else -> Icons.Default.Build
    }
}
