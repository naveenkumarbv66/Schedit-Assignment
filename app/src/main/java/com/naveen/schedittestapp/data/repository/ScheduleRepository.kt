package com.naveen.schedittestapp.data.repository

import com.naveen.schedittestapp.data.dao.EmployeeDao
import com.naveen.schedittestapp.data.dao.ShiftAssignmentDao
import com.naveen.schedittestapp.data.dao.ShiftDao
import com.naveen.schedittestapp.data.dao.ShiftTemplateDao
import com.naveen.schedittestapp.data.model.Employee
import com.naveen.schedittestapp.data.model.Shift
import com.naveen.schedittestapp.data.model.ShiftAssignment
import com.naveen.schedittestapp.data.model.ShiftTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

sealed class AssignmentResult {
    data object Success : AssignmentResult()
    data class Error(val message: String) : AssignmentResult()
}

class ScheduleRepository(
    private val employeeDao: EmployeeDao,
    private val shiftDao: ShiftDao,
    private val shiftAssignmentDao: ShiftAssignmentDao,
    private val shiftTemplateDao: ShiftTemplateDao
) {
    // Employee operations
    fun getAllEmployees(): Flow<List<Employee>> = employeeDao.getAllEmployees()
    
    suspend fun getEmployeeById(id: Long): Employee? = employeeDao.getEmployeeById(id)
    
    fun searchEmployees(query: String): Flow<List<Employee>> = employeeDao.searchEmployees(query)
    
    suspend fun insertEmployee(employee: Employee): Long = employeeDao.insertEmployee(employee)
    
    // Shift operations
    fun getAllShifts(): Flow<List<Shift>> = shiftDao.getAllShifts()
    
    suspend fun getShiftById(id: Long): Shift? = shiftDao.getShiftById(id)
    
    fun getShiftsByDateRange(startTime: Long, endTime: Long): Flow<List<Shift>> =
        shiftDao.getShiftsByDateRange(startTime, endTime)
    
    fun getShiftsByLocation(location: String): Flow<List<Shift>> =
        shiftDao.getShiftsByLocation(location)
    
    suspend fun insertShift(shift: Shift): Long = shiftDao.insertShift(shift)
    
    suspend fun deleteShift(shiftId: Long) {
        val shift = shiftDao.getShiftById(shiftId)
        if (shift != null) {
            // Delete shift - assignments will be automatically deleted due to CASCADE
            shiftDao.deleteShift(shift)
        }
    }
    
    // Assignment operations
    fun getAllAssignments(): Flow<List<ShiftAssignment>> = shiftAssignmentDao.getAllAssignments()
    
    fun getAssignmentsByEmployee(employeeId: Long): Flow<List<ShiftAssignment>> =
        shiftAssignmentDao.getAssignmentsByEmployee(employeeId)
    
    fun getAssignmentsByShift(shiftId: Long): Flow<List<ShiftAssignment>> =
        shiftAssignmentDao.getAssignmentsByShift(shiftId)
    
    // Get shifts for an employee with shift details
    fun getEmployeeShifts(employeeId: Long): Flow<List<Shift>> {
        return combine(
            getAssignmentsByEmployee(employeeId),
            getAllShifts()
        ) { assignments, shifts ->
            val shiftIds = assignments.map { it.shiftId }.toSet()
            shifts.filter { it.id in shiftIds }
        }
    }
    
    // Get employees for a shift with employee details
    fun getShiftEmployees(shiftId: Long): Flow<List<Employee>> {
        return combine(
            getAssignmentsByShift(shiftId),
            getAllEmployees()
        ) { assignments, employees ->
            val employeeIds = assignments.map { it.employeeId }.toSet()
            employees.filter { it.id in employeeIds }
        }
    }
    
    // Business logic: Assign employee to shift with validation
    suspend fun assignEmployeeToShift(employeeId: Long, shiftId: Long): AssignmentResult {
        val employee = employeeDao.getEmployeeById(employeeId)
            ?: return AssignmentResult.Error("Employee not found")
        
        val shift = shiftDao.getShiftById(shiftId)
            ?: return AssignmentResult.Error("Shift not found")
        
        // Check if already assigned
        val existingAssignment = shiftAssignmentDao.getAssignment(employeeId, shiftId)
        if (existingAssignment != null) {
            return AssignmentResult.Error("Employee is already assigned to this shift")
        }
        
        // Validation 1: Skill matching
        if (!employee.hasAllSkills(shift.requiredSkills)) {
            val missingSkills = shift.requiredSkills.filter { !employee.skills.contains(it) }
            return AssignmentResult.Error(
                "Employee lacks required skills: ${missingSkills.joinToString(", ")}"
            )
        }
        
        // Validation 2: No double-booking (overlapping shifts)
        val allAssignments = shiftAssignmentDao.getAssignmentsByEmployee(employeeId).first()
        val employeeShifts = allAssignments.mapNotNull { assignment ->
            shiftDao.getShiftById(assignment.shiftId)
        }
        
        val overlappingShift = employeeShifts.find { it.overlapsWith(shift) }
        if (overlappingShift != null) {
            return AssignmentResult.Error(
                "Employee is already assigned to an overlapping shift"
            )
        }
        
        // Validation 3: Hour limits
        val validationResult = validateHourLimits(employee, shift, employeeShifts)
        if (validationResult is AssignmentResult.Error) {
            return validationResult
        }
        
        // All validations passed, create assignment
        val assignment = ShiftAssignment(
            employeeId = employeeId,
            shiftId = shiftId
        )
        shiftAssignmentDao.insertAssignment(assignment)
        
        return AssignmentResult.Success
    }
    
    private suspend fun validateHourLimits(
        employee: Employee,
        newShift: Shift,
        existingShifts: List<Shift>
    ): AssignmentResult {
        val shiftDuration = newShift.getDurationHours()
        
        // Helper function to convert epoch seconds to LocalDate (compatible with older Android versions)
        fun epochSecondsToLocalDate(epochSeconds: Long): LocalDate {
            return Instant.ofEpochSecond(epochSeconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
        
        // Check daily limit
        val newShiftDate = epochSecondsToLocalDate(newShift.startTime)
        
        val sameDayShifts = existingShifts.filter { shift ->
            val shiftDate = epochSecondsToLocalDate(shift.startTime)
            shiftDate == newShiftDate
        }
        
        val dailyHours = sameDayShifts.sumOf { it.getDurationHours() } + shiftDuration
        if (dailyHours > employee.maxDailyHours) {
            return AssignmentResult.Error(
                "Assignment would exceed daily hour limit (${employee.maxDailyHours}h). " +
                        "Current daily hours: ${dailyHours.toInt()}h"
            )
        }
        
        // Check weekly limit
        val weekStart = newShiftDate.with(java.time.DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(6)
        
        val weeklyShifts = existingShifts.filter { shift ->
            val shiftDate = epochSecondsToLocalDate(shift.startTime)
            shiftDate.isAfter(weekStart.minusDays(1)) && shiftDate.isBefore(weekEnd.plusDays(1))
        }
        
        val weeklyHours = weeklyShifts.sumOf { it.getDurationHours() } + shiftDuration
        if (weeklyHours > employee.maxWeeklyHours) {
            return AssignmentResult.Error(
                "Assignment would exceed weekly hour limit (${employee.maxWeeklyHours}h). " +
                        "Current weekly hours: ${weeklyHours.toInt()}h"
            )
        }
        
        return AssignmentResult.Success
    }
    
    suspend fun removeAssignment(employeeId: Long, shiftId: Long) {
        shiftAssignmentDao.deleteAssignment(employeeId, shiftId)
    }
    
    // Get shift with assigned employees count
    suspend fun getShiftWithStaffing(shiftId: Long): Pair<Shift?, Int> {
        val shift = shiftDao.getShiftById(shiftId)
        val assignments = shiftAssignmentDao.getAssignmentsByShift(shiftId).first()
        return Pair(shift, assignments.size)
    }
    
    // Shift Template operations
    fun getAllTemplates(): Flow<List<ShiftTemplate>> = shiftTemplateDao.getAllTemplates()
    
    suspend fun getTemplateById(id: Long): ShiftTemplate? = shiftTemplateDao.getTemplateById(id)
    
    suspend fun insertTemplate(template: ShiftTemplate): Long = shiftTemplateDao.insertTemplate(template)
    
    suspend fun updateTemplate(template: ShiftTemplate) = shiftTemplateDao.updateTemplate(template)
    
    suspend fun deleteTemplate(template: ShiftTemplate) = shiftTemplateDao.deleteTemplate(template)
    
    // Generate shifts from template for a date range
    suspend fun generateShiftsFromTemplate(
        templateId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Long> {
        val template = shiftTemplateDao.getTemplateById(templateId)
            ?: return emptyList()
        
        val generatedShiftIds = mutableListOf<Long>()
        var currentDate = startDate
        
        while (!currentDate.isAfter(endDate)) {
            // Check if template applies to this day
            val shouldCreate = template.dayOfWeek?.let { dayOfWeek ->
                currentDate.dayOfWeek.value == dayOfWeek
            } ?: true // If dayOfWeek is null, create for all days
            
            if (shouldCreate) {
                val startDateTime = currentDate.atTime(template.startHour, template.startMinute)
                val endDateTime = startDateTime.plusHours(template.durationHours.toLong())
                
                val startTime = startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
                val endTime = endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
                
                val shift = Shift(
                    location = template.location,
                    startTime = startTime,
                    endTime = endTime,
                    requiredSkills = template.requiredSkills,
                    minStaffing = template.minStaffing
                )
                
                val shiftId = shiftDao.insertShift(shift)
                generatedShiftIds.add(shiftId)
            }
            
            currentDate = currentDate.plusDays(1)
        }
        
        return generatedShiftIds
    }
}

