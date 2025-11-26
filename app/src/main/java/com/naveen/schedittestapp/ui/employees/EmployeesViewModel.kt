package com.naveen.schedittestapp.ui.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveen.schedittestapp.data.model.Employee
import com.naveen.schedittestapp.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeesUiState(
    val employees: List<Employee> = emptyList(),
    val filteredEmployees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeesUiState())
    val uiState: StateFlow<EmployeesUiState> = _uiState.asStateFlow()

    init {
        loadEmployees()
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getAllEmployees().collect { employees ->
                    _uiState.value = _uiState.value.copy(
                        employees = employees,
                        isLoading = false
                    )
                    applySearchFilter()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load employees"
                )
            }
        }
    }

    fun searchEmployees(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            applySearchFilter()
        } else {
            viewModelScope.launch {
                try {
                    repository.searchEmployees(query).collect { employees ->
                        _uiState.value = _uiState.value.copy(
                            filteredEmployees = employees
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Search failed"
                    )
                }
            }
        }
    }

    private fun applySearchFilter() {
        val state = _uiState.value
        if (state.searchQuery.isBlank()) {
            _uiState.value = state.copy(filteredEmployees = state.employees)
        }
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                repository.insertEmployee(employee)
                // Flow collection will automatically update
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to add employee"
                )
            }
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                repository.updateEmployee(employee)
                // Flow collection will automatically update
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to update employee"
                )
            }
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

