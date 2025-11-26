package com.naveen.schedittestapp.ui.templates

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
import com.naveen.schedittestapp.data.model.ShiftTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftTemplatesScreen(
    onNavigateBack: () -> Unit,
    onTemplateClick: (Long) -> Unit,
    viewModel: ShiftTemplatesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var templateToEdit by remember { mutableStateOf<ShiftTemplate?>(null) }

    LaunchedEffect(uiState.generationResult) {
        uiState.generationResult?.let {
            if (it is GenerationResult.Success) {
                showCreateDialog = false
                templateToEdit = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shift Templates") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        templateToEdit = null
                        showCreateDialog = true 
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Template")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                templateToEdit = null
                showCreateDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Create Template")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Show generation result
            uiState.generationResult?.let { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (result) {
                            is GenerationResult.Success -> MaterialTheme.colorScheme.primaryContainer
                            is GenerationResult.Error -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Text(
                        text = when (result) {
                            is GenerationResult.Success -> "Successfully generated ${result.count} shifts"
                            is GenerationResult.Error -> result.message
                        },
                        modifier = Modifier.padding(16.dp),
                        color = when (result) {
                            is GenerationResult.Success -> MaterialTheme.colorScheme.onPrimaryContainer
                            is GenerationResult.Error -> MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
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
                            Button(onClick = { viewModel.loadTemplates() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                uiState.templates.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "No templates yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Create a template to generate recurring shifts",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(onClick = { showCreateDialog = true }) {
                                Icon(Icons.Default.Add, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Create Template")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.templates) { template ->
                            TemplateCard(
                                template = template,
                                onClick = { onTemplateClick(template.id) },
                                onEdit = { 
                                    templateToEdit = template
                                    showCreateDialog = true 
                                },
                                onDelete = { viewModel.deleteTemplate(template) },
                                onGenerate = { startDate, endDate ->
                                    viewModel.generateShifts(template.id, startDate, endDate)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Create/Edit Template Dialog
    if (showCreateDialog) {
        CreateTemplateDialog(
            template = templateToEdit,
            onDismiss = { 
                showCreateDialog = false
                templateToEdit = null
            },
            onSave = { template ->
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.saveTemplate(template)
                    showCreateDialog = false
                    templateToEdit = null
                }
            }
        )
    }
}

@Composable
fun TemplateCard(
    template: ShiftTemplate,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onGenerate: (startDate: java.time.LocalDate, endDate: java.time.LocalDate) -> Unit
) {
    var showGenerateDialog by remember { mutableStateOf(false) }
    
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
                    text = template.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Text(
                text = "📍 ${template.location}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Time: ${String.format("%02d:%02d", template.startHour, template.startMinute)} - " +
                        "${String.format("%02d:%02d", template.getEndHour(), template.getEndMinute())} " +
                        "(${template.durationHours}h)",
                style = MaterialTheme.typography.bodySmall
            )
            
            if (template.dayOfWeek != null) {
                val dayName = DayOfWeek.of(template.dayOfWeek).name.lowercase().capitalize()
                Text(
                    text = "Day: $dayName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Day: Daily",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Text(
                text = "Skills: ${template.requiredSkills.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Text(
                text = "Min Staffing: ${template.minStaffing}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Button(
                onClick = { showGenerateDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, null)
                Spacer(Modifier.width(8.dp))
                Text("Generate Shifts")
            }
        }
    }
    
    if (showGenerateDialog) {
        GenerateShiftsDialog(
            templateName = template.name,
            onDismiss = { showGenerateDialog = false },
            onGenerate = { startDate, endDate ->
                onGenerate(startDate, endDate)
                showGenerateDialog = false
            }
        )
    }
}

