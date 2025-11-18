package com.cpaptracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cpaptracker.data.models.PartWithReplacement
import com.cpaptracker.ui.components.PartCard
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    partsWithReplacements: List<PartWithReplacement>,
    onMarkPartReplaced: (Long, LocalDate) -> Unit,
    onMarkPartOrdered: (Long, LocalDate) -> Unit,
    onNavigateToAllParts: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CPAP Tracker",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Parts Replacement Dashboard",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary card
            SummaryCard(partsWithReplacements)

            // Parts list
            if (partsWithReplacements.isEmpty()) {
                EmptyState(onNavigateToAllParts)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Upcoming Replacements",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = onNavigateToAllParts) {
                                Text("View All")
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    items(partsWithReplacements) { partWithReplacement ->
                        PartCard(
                            partWithReplacement = partWithReplacement,
                            onMarkReplaced = {
                                onMarkPartReplaced(partWithReplacement.part.id, LocalDate.now())
                            },
                            onOrderPart = {
                                onMarkPartOrdered(partWithReplacement.part.id, LocalDate.now())
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(parts: List<PartWithReplacement>) {
    val overdueParts = parts.count { it.isOverdue }
    val dueSoonParts = parts.count { (it.daysUntilReplacement ?: 0) in 0..7 && !it.isOverdue }
    val orderedParts = parts.count { it.replacement?.isOrdered == true }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                count = overdueParts,
                label = "Overdue",
                icon = Icons.Default.Warning,
                tint = MaterialTheme.colorScheme.error
            )
            SummaryItem(
                count = dueSoonParts,
                label = "Due Soon",
                icon = Icons.Default.Schedule,
                tint = MaterialTheme.colorScheme.tertiary
            )
            SummaryItem(
                count = orderedParts,
                label = "Ordered",
                icon = Icons.Default.ShoppingCart,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun SummaryItem(
    count: Int,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun EmptyState(onNavigateToAllParts: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EventAvailable,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Replacement Tracking Yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start tracking your CPAP parts to get replacement reminders",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNavigateToAllParts) {
            Text("View All Parts")
        }
    }
}
