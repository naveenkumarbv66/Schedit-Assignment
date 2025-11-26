package com.naveen.schedittestapp.ui.employees

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    employeeId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToShift: (Long) -> Unit,
    viewModel: EmployeeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(employeeId) {
        viewModel.loadEmployeeDetails(employeeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employee Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.employee != null) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Employee")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                val errorMessage = uiState.error
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
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
                        Button(onClick = { viewModel.loadEmployeeDetails(employeeId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
            uiState.employee != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        EmployeeInfoCard(employee = uiState.employee!!)
                    }
                    
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(Icons.Default.Edit, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Edit Employee")
                            }
                            Button(
                                onClick = { showDeleteDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Delete, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Delete")
                            }
                        }
                    }
                    
                    item {
                        Text(
                            text = "Assigned Shifts (${uiState.shifts.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (uiState.shifts.isEmpty()) {
                        item {
                            Text(
                                text = "No shifts assigned yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(uiState.shifts) { shift ->
                            ShiftCard(
                                shift = shift,
                                onClick = { onNavigateToShift(shift.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Employee Dialog
    if (showEditDialog && uiState.employee != null) {
        CreateEditEmployeeDialog(
            employee = uiState.employee!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedEmployee ->
                CoroutineScope(Dispatchers.Main).launch {
                    val success = viewModel.updateEmployee(updatedEmployee)
                    if (success) {
                        showEditDialog = false
                    }
                }
            }
        )
    }

    // Delete Employee Dialog
    if (showDeleteDialog && uiState.employee != null) {
        val currentEmployee = uiState.employee!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Employee") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Are you sure you want to delete this employee?")
                    Text(
                        text = currentEmployee.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.shifts.isNotEmpty()) {
                        Text(
                            text = "⚠️ This will also remove all ${uiState.shifts.size} shift assignment(s) for this employee.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = "This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            val deleted = viewModel.deleteEmployee(employeeId)
                            if (deleted) {
                                showDeleteDialog = false
                                onNavigateBack()
                            } else {
                                showDeleteDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmployeeInfoCard(employee: com.naveen.schedittestapp.data.model.Employee) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = when (employee.employmentType) {
                        com.naveen.schedittestapp.data.model.EmploymentType.FULL_TIME ->
                            MaterialTheme.colorScheme.primaryContainer
                        com.naveen.schedittestapp.data.model.EmploymentType.PART_TIME ->
                            MaterialTheme.colorScheme.secondaryContainer
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = employee.employmentType.name.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Divider()
            
            Text(
                text = "Skills",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = employee.skills.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "Max Daily Hours",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${employee.maxDailyHours}h",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = "Max Weekly Hours",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${employee.maxWeeklyHours}h",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
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
            Text(
                text = shift.location,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
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
            
            Text(
                text = "Skills: ${shift.requiredSkills.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val instant = Instant.ofEpochSecond(timestamp)
    val dateTime = instant.atZone(ZoneId.systemDefault())
    return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
}

