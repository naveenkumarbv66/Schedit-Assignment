package com.naveen.schedittestapp.data

import com.naveen.schedittestapp.data.dao.EmployeeDao
import com.naveen.schedittestapp.data.model.Employee
import com.naveen.schedittestapp.data.model.EmploymentType
import com.naveen.schedittestapp.data.model.Shift
import com.naveen.schedittestapp.data.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitialDataProvider @Inject constructor(
    private val repository: ScheduleRepository,
    private val employeeDao: EmployeeDao
) {
    fun initializeData(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            // Check if data already exists
            val existingEmployees = employeeDao.getAllEmployees().first()
            
            if (existingEmployees.isEmpty()) {
                insertSampleData()
            }
        }
    }

    private suspend fun insertSampleData() {
        // Insert employees
        val john = Employee(
            name = "John Smith",
            skills = listOf("Cashier", "Supervisor"),
            employmentType = EmploymentType.FULL_TIME,
            maxWeeklyHours = 40,
            maxDailyHours = 8
        )
        val johnId = repository.insertEmployee(john)

        val jane = Employee(
            name = "Jane Doe",
            skills = listOf("Barista", "Cashier"),
            employmentType = EmploymentType.PART_TIME,
            maxWeeklyHours = 25,
            maxDailyHours = 6
        )
        val janeId = repository.insertEmployee(jane)

        val bob = Employee(
            name = "Bob Wilson",
            skills = listOf("Cook", "Supervisor"),
            employmentType = EmploymentType.FULL_TIME,
            maxWeeklyHours = 40,
            maxDailyHours = 8
        )
        val bobId = repository.insertEmployee(bob)

        // Insert shifts
        val now = LocalDateTime.now()
        val monday = now.with(java.time.DayOfWeek.MONDAY)
        
        // Monday 9AM-5PM, Retail, needs: Cashier, min staff: 2
        val shift1 = Shift(
            location = "Retail",
            startTime = monday.withHour(9).withMinute(0).atZone(ZoneId.systemDefault()).toEpochSecond(),
            endTime = monday.withHour(17).withMinute(0).atZone(ZoneId.systemDefault()).toEpochSecond(),
            requiredSkills = listOf("Cashier"),
            minStaffing = 2
        )
        val shift1Id = repository.insertShift(shift1)

        // Monday 2PM-10PM, Kitchen, needs: Cook, min staff: 1
        val shift2 = Shift(
            location = "Kitchen",
            startTime = monday.withHour(14).withMinute(0).atZone(ZoneId.systemDefault()).toEpochSecond(),
            endTime = monday.withHour(22).withMinute(0).atZone(ZoneId.systemDefault()).toEpochSecond(),
            requiredSkills = listOf("Cook"),
            minStaffing = 1
        )
        val shift2Id = repository.insertShift(shift2)

        // Tuesday 6AM-2PM, Cafe, needs: Barista, min staff: 2
        val tuesday = monday.plusDays(1)
        val shift3 = Shift(
            location = "Cafe",
            startTime = tuesday.withHour(6).withMinute(0).atZone(ZoneId.systemDefault()).toEpochSecond(),
            endTime = tuesday.withHour(14).withMinute(0).atZone(ZoneId.systemDefault()).toEpochSecond(),
            requiredSkills = listOf("Barista"),
            minStaffing = 2
        )
        val shift3Id = repository.insertShift(shift3)

        // Add some sample assignments
        repository.assignEmployeeToShift(johnId, shift1Id)
        repository.assignEmployeeToShift(janeId, shift1Id)
        repository.assignEmployeeToShift(bobId, shift2Id)
        repository.assignEmployeeToShift(janeId, shift3Id)
    }
}

