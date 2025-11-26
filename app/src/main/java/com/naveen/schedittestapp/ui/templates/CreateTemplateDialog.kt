package com.naveen.schedittestapp.ui.templates

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
import com.naveen.schedittestapp.data.model.ShiftTemplate
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTemplateDialog(
    template: ShiftTemplate?,
    onDismiss: () -> Unit,
    onSave: (ShiftTemplate) -> Unit
) {
    val isEditMode = template != null
    
    var name by remember { mutableStateOf(template?.name ?: "") }
    var location by remember { mutableStateOf(template?.location ?: "") }
    var startHour by remember { mutableStateOf(template?.startHour ?: 9) }
    var startMinute by remember { mutableStateOf(template?.startMinute ?: 0) }
    var durationHours by remember { mutableStateOf(template?.durationHours ?: 8) }
    var requiredSkillsText by remember { mutableStateOf(template?.requiredSkills?.joinToString(", ") ?: "") }
    var minStaffing by remember { mutableStateOf(template?.minStaffing ?: 1) }
    var selectedDayOfWeek by remember { mutableStateOf<Int?>(template?.dayOfWeek) }
    var showDayPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditMode) "Edit Template" else "Create Template") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Template Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Label, null) },
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location *") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    singleLine = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startHour.toString(),
                        onValueChange = { 
                            it.toIntOrNull()?.takeIf { h -> h in 0..23 }?.let { startHour = it }
                        },
                        label = { Text("Start Hour") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = startMinute.toString(),
                        onValueChange = { 
                            it.toIntOrNull()?.takeIf { m -> m in 0..59 }?.let { startMinute = it }
                        },
                        label = { Text("Minute") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                
                OutlinedTextField(
                    value = durationHours.toString(),
                    onValueChange = { 
                        it.toIntOrNull()?.takeIf { d -> d > 0 }?.let { durationHours = it }
                    },
                    label = { Text("Duration (hours)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Timer, null) },
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = requiredSkillsText,
                    onValueChange = { requiredSkillsText = it },
                    label = { Text("Required Skills (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Work, null) },
                    placeholder = { Text("e.g., Cashier, Supervisor") },
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = minStaffing.toString(),
                    onValueChange = { 
                        it.toIntOrNull()?.takeIf { m -> m > 0 }?.let { minStaffing = it }
                    },
                    label = { Text("Minimum Staffing") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.People, null) },
                    singleLine = true
                )
                
                // Day of week selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Day of Week:",
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedDayOfWeek == null,
                        onClick = { selectedDayOfWeek = null },
                        label = { Text("Daily") }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DayOfWeek.values().forEach { day ->
                        FilterChip(
                            selected = selectedDayOfWeek == day.value,
                            onClick = { 
                                selectedDayOfWeek = if (selectedDayOfWeek == day.value) null else day.value
                            },
                            label = { Text(day.name.take(3)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val skills = requiredSkillsText.split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    
                    val newTemplate = ShiftTemplate(
                        id = template?.id ?: 0L,
                        name = name,
                        location = location,
                        startHour = startHour,
                        startMinute = startMinute,
                        durationHours = durationHours,
                        requiredSkills = skills,
                        minStaffing = minStaffing,
                        dayOfWeek = selectedDayOfWeek
                    )
                    onSave(newTemplate)
                },
                enabled = name.isNotBlank() && location.isNotBlank()
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

