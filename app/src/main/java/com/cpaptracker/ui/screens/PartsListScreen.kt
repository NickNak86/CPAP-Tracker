package com.cpaptracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cpaptracker.data.models.PartWithReplacement
import com.cpaptracker.ui.components.PartCard
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartsListScreen(
    parts: List<PartWithReplacement>,
    onMarkPartReplaced: (Long, LocalDate) -> Unit,
    onMarkPartOrdered: (Long, LocalDate) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "All Parts",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${parts.size} parts tracked",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(parts) { partWithReplacement ->
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
