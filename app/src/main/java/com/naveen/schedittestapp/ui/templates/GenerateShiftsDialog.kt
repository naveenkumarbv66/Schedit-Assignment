package com.naveen.schedittestapp.ui.templates

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateShiftsDialog(
    templateName: String,
    onDismiss: () -> Unit,
    onGenerate: (startDate: LocalDate, endDate: LocalDate) -> Unit
) {
    var startDateText by remember { mutableStateOf("") }
    var endDateText by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Shifts from Template") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Template: $templateName",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = startDateText,
                    onValueChange = { startDateText = it },
                    label = { Text("Start Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    placeholder = { Text("2024-01-01") },
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.DateRange, null)
                        }
                    },
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = endDateText,
                    onValueChange = { endDateText = it },
                    label = { Text("End Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    placeholder = { Text("2024-01-31") },
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.DateRange, null)
                        }
                    },
                    singleLine = true
                )
                
                Text(
                    text = "Shifts will be generated for the selected date range based on the template settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val start = try {
                        LocalDate.parse(startDateText)
                    } catch (e: Exception) {
                        null
                    }
                    val end = try {
                        LocalDate.parse(endDateText)
                    } catch (e: Exception) {
                        null
                    }
                    
                    if (start != null && end != null && !start.isAfter(end)) {
                        onGenerate(start, end)
                    }
                },
                enabled = startDateText.isNotBlank() && endDateText.isNotBlank()
            ) {
                Text("Generate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    // Date pickers (simplified - in production, use proper date picker)
    if (showStartDatePicker) {
        // For now, just show a message - in production use DatePicker
        // This is a simplified version
    }
}

