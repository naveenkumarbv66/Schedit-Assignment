package com.naveen.schedittestapp.ui.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveen.schedittestapp.data.model.ShiftTemplate
import com.naveen.schedittestapp.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ShiftTemplatesUiState(
    val templates: List<ShiftTemplate> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val generationResult: GenerationResult? = null
)

sealed class GenerationResult {
    data class Success(val count: Int) : GenerationResult()
    data class Error(val message: String) : GenerationResult()
}

@HiltViewModel
class ShiftTemplatesViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShiftTemplatesUiState())
    val uiState: StateFlow<ShiftTemplatesUiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    fun loadTemplates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getAllTemplates().collect { templates ->
                    _uiState.value = _uiState.value.copy(
                        templates = templates,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load templates"
                )
            }
        }
    }

    suspend fun saveTemplate(template: ShiftTemplate): Long {
        return if (template.id == 0L) {
            repository.insertTemplate(template)
        } else {
            repository.updateTemplate(template)
            template.id
        }
    }

    fun deleteTemplate(template: ShiftTemplate) {
        viewModelScope.launch {
            try {
                repository.deleteTemplate(template)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete template"
                )
            }
        }
    }

    fun generateShifts(templateId: Long, startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(generationResult = null, isLoading = true)
            try {
                val shiftIds = repository.generateShiftsFromTemplate(templateId, startDate, endDate)
                _uiState.value = _uiState.value.copy(
                    generationResult = GenerationResult.Success(shiftIds.size),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    generationResult = GenerationResult.Error(
                        e.message ?: "Failed to generate shifts"
                    ),
                    isLoading = false
                )
            }
        }
    }

    fun clearGenerationResult() {
        _uiState.value = _uiState.value.copy(generationResult = null)
    }
}

