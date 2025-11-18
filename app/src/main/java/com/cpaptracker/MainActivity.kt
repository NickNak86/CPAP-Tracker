package com.cpaptracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cpaptracker.ui.screens.DashboardScreen
import com.cpaptracker.ui.screens.PartsListScreen
import com.cpaptracker.ui.theme.CPAPTrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private val repository by lazy { (application as CPAPTrackerApp).repository }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            CPAPTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CPAPTrackerNavigation(repository)
                }
            }
        }
    }
}

@Composable
fun CPAPTrackerNavigation(
    repository: com.cpaptracker.data.repository.CPAPRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            if (currentRoute == "dashboard" || currentRoute == "parts_list") {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        label = { Text("Dashboard") },
                        selected = currentRoute == "dashboard",
                        onClick = {
                            if (currentRoute != "dashboard") {
                                navController.navigate("dashboard") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Build, contentDescription = null) },
                        label = { Text("All Parts") },
                        selected = currentRoute == "parts_list",
                        onClick = {
                            if (currentRoute != "parts_list") {
                                navController.navigate("parts_list")
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("dashboard") {
                val upcomingReplacements by repository.getUpcomingReplacements()
                    .collectAsState(initial = emptyList())

                DashboardScreen(
                    partsWithReplacements = upcomingReplacements,
                    onMarkPartReplaced = { partId, date ->
                        scope.launch(Dispatchers.IO) {
                            repository.markPartAsReplaced(partId, date)
                        }
                    },
                    onMarkPartOrdered = { partId, date ->
                        scope.launch(Dispatchers.IO) {
                            repository.markPartAsOrdered(partId, date)
                        }
                    },
                    onNavigateToAllParts = {
                        navController.navigate("parts_list")
                    }
                )
            }

            composable("parts_list") {
                val allParts by repository.getPartsWithReplacements()
                    .collectAsState(initial = emptyList())

                PartsListScreen(
                    parts = allParts,
                    onMarkPartReplaced = { partId, date ->
                        scope.launch(Dispatchers.IO) {
                            repository.markPartAsReplaced(partId, date)
                        }
                    },
                    onMarkPartOrdered = { partId, date ->
                        scope.launch(Dispatchers.IO) {
                            repository.markPartAsOrdered(partId, date)
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
