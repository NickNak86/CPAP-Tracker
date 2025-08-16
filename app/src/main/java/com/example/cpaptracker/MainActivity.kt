package com.example.cpaptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cpaptracker.ui.theme.CPAPTrackerTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import android.content.Context

// Data class to represent a single CPAP usage record.
data class CPAPEntry(
    val date: String,
    val time: String
)

// DataStore instance for saving data.
// We are using a simple DataStore with a single key for all our data.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cpap_data")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CPAPTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Call the main composable for the app
                    CPAPTrackerScreen(dataStore)
                }
            }
        }
    }
}

// Key for our data in DataStore
val CPAP_ENTRIES_KEY = stringPreferencesKey("cpap_entries")

// Main UI screen for the app
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CPAPTrackerScreen(dataStore: DataStore<Preferences>) {
    // Coroutine scope to handle suspend functions
    val coroutineScope = rememberCoroutineScope()
    // State to hold the list of CPAP entries
    var cpapEntries by remember { mutableStateOf(listOf<CPAPEntry>()) }

    // Load data when the app starts
    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            cpapEntries = loadEntries(dataStore)
        }
    }

    // State for the text fields
    var dateText by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var timeText by remember { mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // App title
        Text(text = "CPAP Tracker", style = MaterialTheme.typography.headlineLarge)

        // Input fields for date and time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("Date (YYYY-MM-DD)") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = timeText,
                onValueChange = { timeText = it },
                label = { Text("Time (HH:MM)") },
                modifier = Modifier.weight(1f)
            )
        }

        // Save button
        Button(
            onClick = {
                val newEntry = CPAPEntry(dateText, timeText)
                val updatedEntries = cpapEntries + newEntry
                cpapEntries = updatedEntries
                coroutineScope.launch {
                    saveEntries(dataStore, updatedEntries)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save CPAP Usage")
        }

        // List of saved entries
        Text(text = "Saved Entries", style = MaterialTheme.typography.titleLarge)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(cpapEntries) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = entry.date)
                        Text(text = entry.time)
                    }
                }
            }
        }
    }
}

// Suspend function to save the list of entries to DataStore
suspend fun saveEntries(dataStore: DataStore<Preferences>, entries: List<CPAPEntry>) {
    dataStore.edit { preferences ->
        val json = Gson().toJson(entries)
        preferences[CPAP_ENTRIES_KEY] = json
    }
}

// Suspend function to load the list of entries from DataStore
suspend fun loadEntries(dataStore: DataStore<Preferences>): List<CPAPEntry> {
    val preferences = dataStore.data.first()
    val json = preferences[CPAP_ENTRIES_KEY]
    return if (json != null) {
        val type = object : TypeToken<List<CPAPEntry>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}