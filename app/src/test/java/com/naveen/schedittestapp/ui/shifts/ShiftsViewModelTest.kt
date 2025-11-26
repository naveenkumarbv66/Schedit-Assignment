package com.naveen.schedittestapp.ui.shifts

import com.naveen.schedittestapp.data.model.Shift
import com.naveen.schedittestapp.data.repository.ScheduleRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ShiftsViewModelTest {

    private lateinit var repository: ScheduleRepository
    private lateinit var viewModel: ShiftsViewModel

    @Before
    fun setup() {
        repository = mockk()
        viewModel = ShiftsViewModel(repository)
    }

    @Test
    fun `filterByLocation - filters shifts correctly`() = runTest {
        // Given
        val shifts = listOf(
            Shift(id = 1L, location = "Retail", startTime = 1000L, endTime = 2000L, requiredSkills = emptyList()),
            Shift(id = 2L, location = "Kitchen", startTime = 3000L, endTime = 4000L, requiredSkills = emptyList()),
            Shift(id = 3L, location = "Retail Store", startTime = 5000L, endTime = 6000L, requiredSkills = emptyList())
        )
        every { repository.getAllShifts() } returns flowOf(shifts)

        // When
        viewModel.filterByLocation("Retail")

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(2, uiState.filteredShifts.size)
        assertTrue(uiState.filteredShifts.all { it.location.contains("Retail", ignoreCase = true) })
    }

    @Test
    fun `clearFilters - resets all filters`() = runTest {
        // Given
        val shifts = listOf(
            Shift(id = 1L, location = "Retail", startTime = 1000L, endTime = 2000L, requiredSkills = emptyList())
        )
        every { repository.getAllShifts() } returns flowOf(shifts)
        viewModel.filterByLocation("Retail")

        // When
        viewModel.clearFilters()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals("", uiState.filterLocation)
        assertNull(uiState.filterDate)
    }
}

