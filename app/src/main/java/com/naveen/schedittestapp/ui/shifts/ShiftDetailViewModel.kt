package com.naveen.schedittestapp.ui.shifts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveen.schedittestapp.data.model.Employee
import com.naveen.schedittestapp.data.model.Shift
import com.naveen.schedittestapp.data.repository.AssignmentResult
import com.naveen.schedittestapp.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShiftDetailUiState(
    val shift: Shift? = null,
    val assignedEmployees: List<Employee> = emptyList(),
    val availableEmployees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val assignmentResult: AssignmentResult? = null
)

@HiltViewModel
class ShiftDetailViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShiftDetailUiState())
    val uiState: StateFlow<ShiftDetailUiState> = _uiState.asStateFlow()

    fun loadShiftDetails(shiftId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val shift = repository.getShiftById(shiftId)
                if (shift != null) {
                    repository.getShiftEmployees(shiftId).collect { employees ->
                        _uiState.value = _uiState.value.copy(
                            shift = shift,
                            assignedEmployees = employees,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Shift not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load shift details"
                )
            }
        }
    }

    fun loadAvailableEmployees() {
        viewModelScope.launch {
            try {
                repository.getAllEmployees().collect { employees ->
                    _uiState.value = _uiState.value.copy(availableEmployees = employees)
                }
            } catch (e: Exception) {
                // Handle error silently or show in UI
            }
        }
    }

    fun assignEmployee(employeeId: Long, shiftId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(assignmentResult = null)
            try {
                val result = repository.assignEmployeeToShift(employeeId, shiftId)
                _uiState.value = _uiState.value.copy(assignmentResult = result)
                
                if (result is AssignmentResult.Success) {
                    // Reload shift details to show updated assignments
                    loadShiftDetails(shiftId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    assignmentResult = AssignmentResult.Error(
                        e.message ?: "Failed to assign employee"
                    )
                )
            }
        }
    }

    fun removeAssignment(employeeId: Long, shiftId: Long) {
        viewModelScope.launch {
            try {
                repository.removeAssignment(employeeId, shiftId)
                loadShiftDetails(shiftId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to remove assignment"
                )
            }
        }
    }

    fun clearAssignmentResult() {
        _uiState.value = _uiState.value.copy(assignmentResult = null)
    }

    suspend fun deleteShift(shiftId: Long): Boolean {
        return try {
            repository.deleteShift(shiftId)
            true
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = e.message ?: "Failed to delete shift"
            )
            false
        }
    }
}

