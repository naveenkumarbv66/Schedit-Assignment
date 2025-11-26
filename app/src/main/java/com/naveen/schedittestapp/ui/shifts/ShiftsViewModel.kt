package com.naveen.schedittestapp.ui.shifts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveen.schedittestapp.data.model.Shift
import com.naveen.schedittestapp.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class ShiftUiState(
    val shifts: List<Shift> = emptyList(),
    val filteredShifts: List<Shift> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterLocation: String = "",
    val filterDate: LocalDate? = null
)

@HiltViewModel
class ShiftsViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShiftUiState())
    val uiState: StateFlow<ShiftUiState> = _uiState.asStateFlow()

    init {
        loadShifts()
    }

    fun loadShifts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getAllShifts().collect { shifts ->
                    _uiState.value = _uiState.value.copy(
                        shifts = shifts,
                        isLoading = false
                    )
                    applyFilters()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load shifts"
                )
            }
        }
    }

    fun filterByLocation(location: String) {
        _uiState.value = _uiState.value.copy(filterLocation = location)
        applyFilters()
    }

    fun filterByDate(date: LocalDate?) {
        _uiState.value = _uiState.value.copy(filterDate = date)
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.shifts

        // Filter by location
        if (state.filterLocation.isNotBlank()) {
            filtered = filtered.filter {
                it.location.contains(state.filterLocation, ignoreCase = true)
            }
        }

        // Filter by date
        if (state.filterDate != null) {
            val startOfDay = state.filterDate.atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond()
            val endOfDay = startOfDay + ChronoUnit.DAYS.duration.seconds

            filtered = filtered.filter { shift ->
                shift.startTime >= startOfDay && shift.startTime < endOfDay
            }
        }

        _uiState.value = state.copy(filteredShifts = filtered)
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            filterLocation = "",
            filterDate = null
        )
        applyFilters()
    }
}

