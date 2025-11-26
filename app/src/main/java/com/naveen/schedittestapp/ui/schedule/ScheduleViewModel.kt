package com.naveen.schedittestapp.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveen.schedittestapp.data.model.Employee
import com.naveen.schedittestapp.data.model.Shift
import com.naveen.schedittestapp.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class ScheduleEntry(
    val shift: Shift,
    val employees: List<Employee>
)

data class ScheduleUiState(
    val scheduleEntries: List<ScheduleEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val viewMode: ScheduleViewMode = ScheduleViewMode.WEEKLY,
    val selectedDate: LocalDate = LocalDate.now()
)

enum class ScheduleViewMode {
    WEEKLY,
    DAILY,
    EMPLOYEE_CENTRIC,
    SHIFT_CENTRIC
}

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadSchedule()
    }

    fun loadSchedule() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                when (_uiState.value.viewMode) {
                    ScheduleViewMode.WEEKLY -> loadWeeklySchedule()
                    ScheduleViewMode.DAILY -> loadDailySchedule()
                    ScheduleViewMode.EMPLOYEE_CENTRIC -> loadEmployeeCentricSchedule()
                    ScheduleViewMode.SHIFT_CENTRIC -> loadShiftCentricSchedule()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load schedule"
                )
            }
        }
    }

    private fun loadWeeklySchedule() {
        viewModelScope.launch {
            val selectedDate = _uiState.value.selectedDate
            val weekStart = selectedDate.with(java.time.DayOfWeek.MONDAY)
            val weekEnd = weekStart.plusDays(6)
            
            val startTime = weekStart.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
            val endTime = weekEnd.atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault()).toEpochSecond()

            repository.getShiftsByDateRange(startTime, endTime).collect { shifts ->
                loadScheduleEntries(shifts)
            }
        }
    }

    private fun loadDailySchedule() {
        viewModelScope.launch {
            val selectedDate = _uiState.value.selectedDate
            val startTime = selectedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
            val endTime = selectedDate.atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault()).toEpochSecond()

            repository.getShiftsByDateRange(startTime, endTime).collect { shifts ->
                loadScheduleEntries(shifts)
            }
        }
    }

    private fun loadEmployeeCentricSchedule() {
        viewModelScope.launch {
            combine(
                repository.getAllEmployees(),
                repository.getAllShifts(),
                repository.getAllAssignments()
            ) { employees, shifts, assignments ->
                val employeeShiftsMap = mutableMapOf<Long, MutableList<Shift>>()
                
                assignments.forEach { assignment ->
                    val shift = shifts.find { it.id == assignment.shiftId }
                    if (shift != null) {
                        employeeShiftsMap.getOrPut(assignment.employeeId) { mutableListOf() }
                            .add(shift)
                    }
                }
                
                // For employee-centric view, we show all employees with their shifts
                val entries = employees.map { employee ->
                    val employeeShifts = employeeShiftsMap[employee.id] ?: emptyList()
                    ScheduleEntry(
                        shift = employeeShifts.firstOrNull() ?: createDummyShift(),
                        employees = listOf(employee)
                    )
                }
                
                entries
            }.collect { entries ->
                _uiState.value = _uiState.value.copy(
                    scheduleEntries = entries,
                    isLoading = false
                )
            }
        }
    }

    private fun loadShiftCentricSchedule() {
        viewModelScope.launch {
            combine(
                repository.getAllShifts(),
                repository.getAllAssignments(),
                repository.getAllEmployees()
            ) { shifts, assignments, employees ->
                val entries = shifts.map { shift ->
                    val assignedEmployeeIds = assignments
                        .filter { it.shiftId == shift.id }
                        .map { it.employeeId }
                    val assignedEmployees = employees.filter { it.id in assignedEmployeeIds }
                    
                    ScheduleEntry(shift, assignedEmployees)
                }
                
                entries
            }.collect { entries ->
                _uiState.value = _uiState.value.copy(
                    scheduleEntries = entries,
                    isLoading = false
                )
            }
        }
    }

    private fun loadScheduleEntries(shifts: List<Shift>) {
        viewModelScope.launch {
            if (shifts.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    scheduleEntries = emptyList(),
                    isLoading = false
                )
                return@launch
            }
            
            // For weekly/daily views, use combine with assignments and employees
            combine(
                repository.getAllAssignments(),
                repository.getAllEmployees()
            ) { assignments, employees ->
                shifts.map { shift ->
                    val assignedEmployeeIds = assignments
                        .filter { it.shiftId == shift.id }
                        .map { it.employeeId }
                    val assignedEmployees = employees.filter { it.id in assignedEmployeeIds }
                    ScheduleEntry(shift, assignedEmployees)
                }.sortedBy { it.shift.startTime }
            }.collect { entries ->
                _uiState.value = _uiState.value.copy(
                    scheduleEntries = entries,
                    isLoading = false
                )
            }
        }
    }

    fun setViewMode(mode: ScheduleViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = mode)
        loadSchedule()
    }

    fun setSelectedDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadSchedule()
    }

    private fun createDummyShift(): Shift {
        return Shift(
            id = -1,
            location = "",
            startTime = 0,
            endTime = 0,
            requiredSkills = emptyList()
        )
    }
}

