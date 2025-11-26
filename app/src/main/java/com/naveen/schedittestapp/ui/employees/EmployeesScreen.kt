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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen(
    onEmployeeClick: (Long) -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToShifts: () -> Unit,
    onNavigateToAssignment: () -> Unit,
    viewModel: EmployeesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddEmployeeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employees") },
                actions = {
                    IconButton(onClick = { showAddEmployeeDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Employee")
                    }
                    IconButton(onClick = onNavigateToSchedule) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Schedule")
                    }
                    IconButton(onClick = onNavigateToShifts) {
                        Icon(Icons.Default.Work, contentDescription = "Shifts")
                    }
                    IconButton(onClick = onNavigateToAssignment) {
                        Icon(Icons.Default.Assignment, contentDescription = "Assign")
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
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::searchEmployees,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("Search employees") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.searchEmployees("") }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                singleLine = true
            )

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
                        }
                    }
                }
                uiState.filteredEmployees.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No employees found")
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.filteredEmployees) { employee ->
                            EmployeeCard(
                                employee = employee,
                                onClick = { onEmployeeClick(employee.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Employee Dialog
    if (showAddEmployeeDialog) {
        CreateEditEmployeeDialog(
            employee = null,
            onDismiss = { showAddEmployeeDialog = false },
            onSave = { employee ->
                viewModel.addEmployee(employee)
                showAddEmployeeDialog = false
            }
        )
    }
}

@Composable
fun EmployeeCard(
    employee: com.naveen.schedittestapp.data.model.Employee,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleLarge,
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
                    text = employee.skills.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Max Daily: ${employee.maxDailyHours}h",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Max Weekly: ${employee.maxWeeklyHours}h",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

