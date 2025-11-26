package com.naveen.schedittestapp.ui.schedule

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
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onNavigateToShift: (Long) -> Unit,
    onNavigateToEmployee: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ViewModule, contentDescription = "View Mode")
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Weekly View") },
                                onClick = {
                                    viewModel.setViewMode(ScheduleViewMode.WEEKLY)
                                    expanded = false
                                },
                                leadingIcon = {
                                    if (uiState.viewMode == ScheduleViewMode.WEEKLY) {
                                        Icon(Icons.Default.Check, null)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Daily View") },
                                onClick = {
                                    viewModel.setViewMode(ScheduleViewMode.DAILY)
                                    expanded = false
                                },
                                leadingIcon = {
                                    if (uiState.viewMode == ScheduleViewMode.DAILY) {
                                        Icon(Icons.Default.Check, null)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("By Employee") },
                                onClick = {
                                    viewModel.setViewMode(ScheduleViewMode.EMPLOYEE_CENTRIC)
                                    expanded = false
                                },
                                leadingIcon = {
                                    if (uiState.viewMode == ScheduleViewMode.EMPLOYEE_CENTRIC) {
                                        Icon(Icons.Default.Check, null)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("By Shift") },
                                onClick = {
                                    viewModel.setViewMode(ScheduleViewMode.SHIFT_CENTRIC)
                                    expanded = false
                                },
                                leadingIcon = {
                                    if (uiState.viewMode == ScheduleViewMode.SHIFT_CENTRIC) {
                                        Icon(Icons.Default.Check, null)
                                    }
                                }
                            )
                        }
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
            // View mode selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.viewMode == ScheduleViewMode.WEEKLY,
                    onClick = { viewModel.setViewMode(ScheduleViewMode.WEEKLY) },
                    label = { Text("Weekly") }
                )
                FilterChip(
                    selected = uiState.viewMode == ScheduleViewMode.DAILY,
                    onClick = { viewModel.setViewMode(ScheduleViewMode.DAILY) },
                    label = { Text("Daily") }
                )
                FilterChip(
                    selected = uiState.viewMode == ScheduleViewMode.EMPLOYEE_CENTRIC,
                    onClick = { viewModel.setViewMode(ScheduleViewMode.EMPLOYEE_CENTRIC) },
                    label = { Text("By Employee") }
                )
                FilterChip(
                    selected = uiState.viewMode == ScheduleViewMode.SHIFT_CENTRIC,
                    onClick = { viewModel.setViewMode(ScheduleViewMode.SHIFT_CENTRIC) },
                    label = { Text("By Shift") }
                )
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
                            Button(onClick = { viewModel.loadSchedule() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                uiState.scheduleEntries.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No schedule entries found")
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.scheduleEntries) { entry ->
                            ScheduleEntryCard(
                                entry = entry,
                                viewMode = uiState.viewMode,
                                onShiftClick = { onNavigateToShift(entry.shift.id) },
                                onEmployeeClick = { employeeId ->
                                    onNavigateToEmployee(employeeId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleEntryCard(
    entry: ScheduleEntry,
    viewMode: ScheduleViewMode,
    onShiftClick: () -> Unit,
    onEmployeeClick: (Long) -> Unit
) {
    Card(
        onClick = onShiftClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (viewMode) {
                ScheduleViewMode.EMPLOYEE_CENTRIC -> {
                    if (entry.employees.isNotEmpty()) {
                        Text(
                            text = entry.employees[0].name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (entry.shift.id != -1L) {
                            Text(
                                text = "${entry.shift.location} - ${formatTime(entry.shift.startTime)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = "No shifts assigned",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        text = entry.shift.location,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(entry.shift.startTime),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "→",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = formatTime(entry.shift.endTime),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    if (entry.employees.isNotEmpty()) {
                        Text(
                            text = "Assigned Employees (${entry.employees.size}):",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        entry.employees.forEach { employee ->
                            TextButton(
                                onClick = { onEmployeeClick(employee.id) },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "• ${employee.name}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No employees assigned",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val instant = Instant.ofEpochSecond(timestamp)
    val dateTime = instant.atZone(ZoneId.systemDefault())
    return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
}

