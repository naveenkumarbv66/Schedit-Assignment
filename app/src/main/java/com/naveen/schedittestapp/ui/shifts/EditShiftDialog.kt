package com.naveen.schedittestapp.ui.shifts

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
import com.naveen.schedittestapp.data.model.Shift
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditShiftDialog(
    shift: Shift,
    onDismiss: () -> Unit,
    onSave: (Shift) -> Unit
) {
    val startDateTime = Instant.ofEpochSecond(shift.startTime)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    val endDateTime = Instant.ofEpochSecond(shift.endTime)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    var location by remember { mutableStateOf(shift.location) }
    var startDateText by remember { mutableStateOf(startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var startTimeText by remember { mutableStateOf(startDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))) }
    var endTimeText by remember { mutableStateOf(endDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))) }
    var requiredSkillsText by remember { mutableStateOf(shift.requiredSkills.joinToString(", ")) }
    var minStaffing by remember { mutableStateOf(shift.minStaffing.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Shift") },
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
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location *") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    singleLine = true
                )

                OutlinedTextField(
                    value = startDateText,
                    onValueChange = { startDateText = it },
                    label = { Text("Start Date (YYYY-MM-DD) *") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    placeholder = { Text("2024-01-15") },
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTimeText,
                        onValueChange = { startTimeText = it },
                        label = { Text("Start Time (HH:mm) *") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        placeholder = { Text("09:00") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = endTimeText,
                        onValueChange = { endTimeText = it },
                        label = { Text("End Time (HH:mm) *") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        placeholder = { Text("17:00") },
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = requiredSkillsText,
                    onValueChange = { requiredSkillsText = it },
                    label = { Text("Required Skills (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Work, null) },
                    placeholder = { Text("Cashier, Supervisor") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = minStaffing,
                    onValueChange = { 
                        if (it.all { char -> char.isDigit() }) {
                            minStaffing = it
                        }
                    },
                    label = { Text("Minimum Staffing *") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.People, null) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    errorMessage = null
                    try {
                        // Parse date and time
                        val startDate = LocalDate.parse(startDateText)
                        val startTimeParts = startTimeText.split(":")
                        val endTimeParts = endTimeText.split(":")
                        
                        if (startTimeParts.size != 2 || endTimeParts.size != 2) {
                            errorMessage = "Invalid time format. Use HH:mm"
                            return@TextButton
                        }
                        
                        val startHour = startTimeParts[0].toInt()
                        val startMinute = startTimeParts[1].toInt()
                        val endHour = endTimeParts[0].toInt()
                        val endMinute = endTimeParts[1].toInt()
                        
                        if (startHour !in 0..23 || startMinute !in 0..59 ||
                            endHour !in 0..23 || endMinute !in 0..59) {
                            errorMessage = "Invalid time values"
                            return@TextButton
                        }
                        
                        val startDateTime = startDate.atTime(startHour, startMinute)
                            .atZone(ZoneId.systemDefault())
                        val endDateTime = startDate.atTime(endHour, endMinute)
                            .atZone(ZoneId.systemDefault())
                        
                        if (endDateTime.toEpochSecond() <= startDateTime.toEpochSecond()) {
                            errorMessage = "End time must be after start time"
                            return@TextButton
                        }
                        
                        val skills = requiredSkillsText.split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        
                        val minStaffingValue = minStaffing.toIntOrNull() ?: 1
                        
                        if (location.isBlank()) {
                            errorMessage = "Location is required"
                            return@TextButton
                        }
                        
                        val updatedShift = shift.copy(
                            location = location,
                            startTime = startDateTime.toEpochSecond(),
                            endTime = endDateTime.toEpochSecond(),
                            requiredSkills = skills,
                            minStaffing = minStaffingValue
                        )
                        
                        onSave(updatedShift)
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Invalid input. Please check all fields."
                    }
                },
                enabled = location.isNotBlank() && 
                         startDateText.isNotBlank() && 
                         startTimeText.isNotBlank() && 
                         endTimeText.isNotBlank()
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

