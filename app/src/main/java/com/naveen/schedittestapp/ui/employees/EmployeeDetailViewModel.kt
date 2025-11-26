package com.naveen.schedittestapp.ui.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveen.schedittestapp.data.model.Employee
import com.naveen.schedittestapp.data.model.Shift
import com.naveen.schedittestapp.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeeDetailUiState(
    val employee: Employee? = null,
    val shifts: List<Shift> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EmployeeDetailViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeDetailUiState())
    val uiState: StateFlow<EmployeeDetailUiState> = _uiState.asStateFlow()

    fun loadEmployeeDetails(employeeId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val employee = repository.getEmployeeById(employeeId)
                if (employee != null) {
                    repository.getEmployeeShifts(employeeId).collect { shifts ->
                        _uiState.value = _uiState.value.copy(
                            employee = employee,
                            shifts = shifts,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Employee not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load employee details"
                )
            }
        }
    }

    suspend fun updateEmployee(employee: Employee): Boolean {
        return try {
            repository.updateEmployee(employee)
            loadEmployeeDetails(employee.id)
            true
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = e.message ?: "Failed to update employee"
            )
            false
        }
    }

    suspend fun deleteEmployee(employeeId: Long): Boolean {
        return try {
            repository.deleteEmployee(employeeId)
            true
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = e.message ?: "Failed to delete employee"
            )
            false
        }
    }
}

