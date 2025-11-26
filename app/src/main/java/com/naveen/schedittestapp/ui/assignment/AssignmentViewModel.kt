package com.naveen.schedittestapp.ui.assignment

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssignmentUiState(
    val employees: List<Employee> = emptyList(),
    val shifts: List<Shift> = emptyList(),
    val selectedEmployee: Employee? = null,
    val selectedShift: Shift? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val assignmentResult: AssignmentResult? = null
)

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssignmentUiState())
    val uiState: StateFlow<AssignmentUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                combine(
                    repository.getAllEmployees(),
                    repository.getAllShifts()
                ) { employees, shifts ->
                    _uiState.value = _uiState.value.copy(
                        employees = employees,
                        shifts = shifts,
                        isLoading = false
                    )
                }.collect {}
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load data"
                )
            }
        }
    }

    fun selectEmployee(employee: Employee?) {
        _uiState.value = _uiState.value.copy(selectedEmployee = employee)
    }

    fun selectShift(shift: Shift?) {
        _uiState.value = _uiState.value.copy(selectedShift = shift)
    }

    fun assignEmployeeToShift() {
        val state = _uiState.value
        val employee = state.selectedEmployee
        val shift = state.selectedShift

        if (employee == null || shift == null) {
            _uiState.value = state.copy(
                assignmentResult = AssignmentResult.Error("Please select both employee and shift")
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(assignmentResult = null, isLoading = true)
            try {
                val result = repository.assignEmployeeToShift(employee.id, shift.id)
                _uiState.value = state.copy(
                    assignmentResult = result,
                    isLoading = false,
                    selectedEmployee = null,
                    selectedShift = null
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    assignmentResult = AssignmentResult.Error(
                        e.message ?: "Failed to assign employee"
                    ),
                    isLoading = false
                )
            }
        }
    }

    fun clearAssignmentResult() {
        _uiState.value = _uiState.value.copy(assignmentResult = null)
    }
}

