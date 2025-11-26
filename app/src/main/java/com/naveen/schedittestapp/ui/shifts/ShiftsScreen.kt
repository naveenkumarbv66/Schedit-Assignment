package com.naveen.schedittestapp.ui.shifts

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
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftsScreen(
    onShiftClick: (Long) -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToEmployees: () -> Unit,
    onNavigateToAssignment: () -> Unit,
    onNavigateToTemplates: () -> Unit = {},
    viewModel: ShiftsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shifts") },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = onNavigateToSchedule) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Schedule")
                    }
                    IconButton(onClick = onNavigateToEmployees) {
                        Icon(Icons.Default.People, contentDescription = "Employees")
                    }
                    IconButton(onClick = onNavigateToAssignment) {
                        Icon(Icons.Default.Assignment, contentDescription = "Assign")
                    }
                    IconButton(onClick = onNavigateToTemplates) {
                        Icon(Icons.Default.Schedule, contentDescription = "Templates")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (showFilters) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.filterLocation,
                            onValueChange = viewModel::filterByLocation,
                            label = { Text("Location") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.LocationOn, null) }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.clearFilters() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Clear Filters")
                            }
                        }
                    }
                }
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    val errorMessage = uiState.error
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { viewModel.loadShifts() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                uiState.filteredShifts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No shifts found")
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.filteredShifts) { shift ->
                            ShiftCard(
                                shift = shift,
                                onClick = { onShiftClick(shift.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShiftCard(
    shift: com.naveen.schedittestapp.data.model.Shift,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Location with icon - prominently displayed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = shift.location,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(shift.startTime),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "→",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatTime(shift.endTime),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Skills: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = shift.requiredSkills.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Text(
                text = "Min Staffing: ${shift.minStaffing}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val instant = Instant.ofEpochSecond(timestamp)
    val dateTime = instant.atZone(ZoneId.systemDefault())
    return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
}

