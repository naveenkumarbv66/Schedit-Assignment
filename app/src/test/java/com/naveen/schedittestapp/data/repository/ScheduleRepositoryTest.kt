package com.naveen.schedittestapp.data.repository

import com.naveen.schedittestapp.data.dao.EmployeeDao
import com.naveen.schedittestapp.data.dao.ShiftAssignmentDao
import com.naveen.schedittestapp.data.dao.ShiftDao
import com.naveen.schedittestapp.data.model.Employee
import com.naveen.schedittestapp.data.model.EmploymentType
import com.naveen.schedittestapp.data.model.Shift
import com.naveen.schedittestapp.data.model.ShiftAssignment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class ScheduleRepositoryTest {

    private lateinit var employeeDao: EmployeeDao
    private lateinit var shiftDao: ShiftDao
    private lateinit var shiftAssignmentDao: ShiftAssignmentDao
    private lateinit var repository: ScheduleRepository

    @Before
    fun setup() {
        employeeDao = mockk()
        shiftDao = mockk()
        shiftAssignmentDao = mockk()
        repository = ScheduleRepository(employeeDao, shiftDao, shiftAssignmentDao)
    }

    @Test
    fun `assignEmployeeToShift - skill mismatch returns error`() = runTest {
        // Given
        val employee = Employee(
            id = 1L,
            name = "John",
            skills = listOf("Cashier"),
            employmentType = EmploymentType.FULL_TIME
        )
        val shift = Shift(
            id = 1L,
            location = "Retail",
            startTime = 1000L,
            endTime = 2000L,
            requiredSkills = listOf("Cook")
        )

        coEvery { employeeDao.getEmployeeById(1L) } returns employee
        coEvery { shiftDao.getShiftById(1L) } returns shift
        coEvery { shiftAssignmentDao.getAssignment(1L, 1L) } returns null
        every { shiftAssignmentDao.getAssignmentsByEmployee(1L) } returns flowOf(emptyList())

        // When
        val result = repository.assignEmployeeToShift(1L, 1L)

        // Then
        assertTrue(result is AssignmentResult.Error)
        assertTrue((result as AssignmentResult.Error).message.contains("lacks required skills"))
        coVerify(exactly = 0) { shiftAssignmentDao.insertAssignment(any()) }
    }

    @Test
    fun `assignEmployeeToShift - overlapping shifts returns error`() = runTest {
        // Given
        val employee = Employee(
            id = 1L,
            name = "John",
            skills = listOf("Cashier"),
            employmentType = EmploymentType.FULL_TIME
        )
        val existingShift = Shift(
            id = 2L,
            location = "Retail",
            startTime = 1000L,
            endTime = 2000L,
            requiredSkills = listOf("Cashier")
        )
        val newShift = Shift(
            id = 1L,
            location = "Retail",
            startTime = 1500L, // Overlaps with existing shift
            endTime = 2500L,
            requiredSkills = listOf("Cashier")
        )

        coEvery { employeeDao.getEmployeeById(1L) } returns employee
        coEvery { shiftDao.getShiftById(1L) } returns newShift
        coEvery { shiftAssignmentDao.getAssignment(1L, 1L) } returns null
        every { shiftAssignmentDao.getAssignmentsByEmployee(1L) } returns flowOf(
            listOf(ShiftAssignment(employeeId = 1L, shiftId = 2L))
        )
        coEvery { shiftDao.getShiftById(2L) } returns existingShift

        // When
        val result = repository.assignEmployeeToShift(1L, 1L)

        // Then
        assertTrue(result is AssignmentResult.Error)
        assertTrue((result as AssignmentResult.Error).message.contains("overlapping"))
        coVerify(exactly = 0) { shiftAssignmentDao.insertAssignment(any()) }
    }

    @Test
    fun `assignEmployeeToShift - exceeds daily hours returns error`() = runTest {
        // Given
        val employee = Employee(
            id = 1L,
            name = "John",
            skills = listOf("Cashier"),
            employmentType = EmploymentType.FULL_TIME,
            maxDailyHours = 8
        )
        
        val sameDay = LocalDateTime.now()
        val existingShift = Shift(
            id = 2L,
            location = "Retail",
            startTime = sameDay.withHour(9).atZone(ZoneId.systemDefault()).toEpochSecond(),
            endTime = sameDay.withHour(17).atZone(ZoneId.systemDefault()).toEpochSecond(), // 8 hours
            requiredSkills = listOf("Cashier")
        )
        val newShift = Shift(
            id = 1L,
            location = "Retail",
            startTime = sameDay.withHour(18).atZone(ZoneId.systemDefault()).toEpochSecond(),
            endTime = sameDay.withHour(19).atZone(ZoneId.systemDefault()).toEpochSecond(), // 1 hour
            requiredSkills = listOf("Cashier")
        )

        coEvery { employeeDao.getEmployeeById(1L) } returns employee
        coEvery { shiftDao.getShiftById(1L) } returns newShift
        coEvery { shiftAssignmentDao.getAssignment(1L, 1L) } returns null
        every { shiftAssignmentDao.getAssignmentsByEmployee(1L) } returns flowOf(
            listOf(ShiftAssignment(employeeId = 1L, shiftId = 2L))
        )
        coEvery { shiftDao.getShiftById(2L) } returns existingShift

        // When
        val result = repository.assignEmployeeToShift(1L, 1L)

        // Then
        assertTrue(result is AssignmentResult.Error)
        assertTrue((result as AssignmentResult.Error).message.contains("daily hour limit"))
        coVerify(exactly = 0) { shiftAssignmentDao.insertAssignment(any()) }
    }

    @Test
    fun `assignEmployeeToShift - valid assignment returns success`() = runTest {
        // Given
        val employee = Employee(
            id = 1L,
            name = "John",
            skills = listOf("Cashier"),
            employmentType = EmploymentType.FULL_TIME
        )
        val shift = Shift(
            id = 1L,
            location = "Retail",
            startTime = 1000L,
            endTime = 2000L,
            requiredSkills = listOf("Cashier")
        )

        coEvery { employeeDao.getEmployeeById(1L) } returns employee
        coEvery { shiftDao.getShiftById(1L) } returns shift
        coEvery { shiftAssignmentDao.getAssignment(1L, 1L) } returns null
        every { shiftAssignmentDao.getAssignmentsByEmployee(1L) } returns flowOf(emptyList())
        coEvery { shiftAssignmentDao.insertAssignment(any()) } returns 1L

        // When
        val result = repository.assignEmployeeToShift(1L, 1L)

        // Then
        assertTrue(result is AssignmentResult.Success)
        coVerify(exactly = 1) { shiftAssignmentDao.insertAssignment(any()) }
    }

    @Test
    fun `getEmployeeShifts - returns correct shifts`() = runTest {
        // Given
        val employeeId = 1L
        val assignments = listOf(
            ShiftAssignment(employeeId = employeeId, shiftId = 1L),
            ShiftAssignment(employeeId = employeeId, shiftId = 2L)
        )
        val shifts = listOf(
            Shift(id = 1L, location = "Retail", startTime = 1000L, endTime = 2000L, requiredSkills = emptyList()),
            Shift(id = 2L, location = "Kitchen", startTime = 3000L, endTime = 4000L, requiredSkills = emptyList())
        )

        every { shiftAssignmentDao.getAssignmentsByEmployee(employeeId) } returns flowOf(assignments)
        every { shiftDao.getAllShifts() } returns flowOf(shifts)

        // When
        val result = repository.getEmployeeShifts(employeeId).first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == 1L })
        assertTrue(result.any { it.id == 2L })
    }
}

