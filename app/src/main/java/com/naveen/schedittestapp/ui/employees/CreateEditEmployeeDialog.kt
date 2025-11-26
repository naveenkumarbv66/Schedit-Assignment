package com.naveen.schedittestapp.ui.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.naveen.schedittestapp.data.model.Employee
import com.naveen.schedittestapp.data.model.EmploymentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditEmployeeDialog(
    employee: Employee?,
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var name by remember { mutableStateOf(employee?.name ?: "") }
    var skillsText by remember { mutableStateOf(employee?.skills?.joinToString(", ") ?: "") }
    var employmentType by remember { mutableStateOf(employee?.employmentType ?: EmploymentType.FULL_TIME) }
    var maxDailyHours by remember { mutableStateOf(employee?.maxDailyHours?.toString() ?: "8") }
    var maxWeeklyHours by remember { mutableStateOf(employee?.maxWeeklyHours?.toString() ?: "40") }
    var showEmploymentTypeMenu by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (employee == null) "Add Employee" else "Edit Employee") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    singleLine = true
                )

                OutlinedTextField(
                    value = skillsText,
                    onValueChange = { skillsText = it },
                    label = { Text("Skills (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Work, null) },
                    placeholder = { Text("Cashier, Supervisor, Barista") },
                    singleLine = true
                )

                // Employment Type Dropdown
                Box {
                    OutlinedTextField(
                        value = employmentType.name.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Employment Type *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Business, null) },
                        trailingIcon = {
                            IconButton(onClick = { showEmploymentTypeMenu = true }) {
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = showEmploymentTypeMenu,
                        onDismissRequest = { showEmploymentTypeMenu = false }
                    ) {
                        EmploymentType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name.replace("_", " ")) },
                                onClick = {
                                    employmentType = type
                                    showEmploymentTypeMenu = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = maxDailyHours,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                maxDailyHours = it
                            }
                        },
                        label = { Text("Max Daily Hours *") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = maxWeeklyHours,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                maxWeeklyHours = it
                            }
                        },
                        label = { Text("Max Weekly Hours *") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    errorMessage = null
                    try {
                        if (name.isBlank()) {
                            errorMessage = "Name is required"
                            return@TextButton
                        }

                        val skills = skillsText.split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }

                        val maxDaily = maxDailyHours.toIntOrNull() ?: 8
                        val maxWeekly = maxWeeklyHours.toIntOrNull() ?: 40

                        if (maxDaily <= 0 || maxWeekly <= 0) {
                            errorMessage = "Hours must be greater than 0"
                            return@TextButton
                        }

                        val updatedEmployee = if (employee == null) {
                            Employee(
                                name = name,
                                skills = skills,
                                employmentType = employmentType,
                                maxDailyHours = maxDaily,
                                maxWeeklyHours = maxWeekly
                            )
                        } else {
                            employee.copy(
                                name = name,
                                skills = skills,
                                employmentType = employmentType,
                                maxDailyHours = maxDaily,
                                maxWeeklyHours = maxWeekly
                            )
                        }

                        onSave(updatedEmployee)
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Invalid input. Please check all fields."
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

