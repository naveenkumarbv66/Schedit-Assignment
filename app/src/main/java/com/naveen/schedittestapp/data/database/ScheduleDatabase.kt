package com.naveen.schedittestapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.naveen.schedittestapp.data.dao.EmployeeDao
import com.naveen.schedittestapp.data.dao.ShiftAssignmentDao
import com.naveen.schedittestapp.data.dao.ShiftDao
import com.naveen.schedittestapp.data.dao.ShiftTemplateDao
import com.naveen.schedittestapp.data.model.Converters
import com.naveen.schedittestapp.data.model.Employee
import com.naveen.schedittestapp.data.model.Shift
import com.naveen.schedittestapp.data.model.ShiftAssignment
import com.naveen.schedittestapp.data.model.ShiftTemplate

@Database(
    entities = [Employee::class, Shift::class, ShiftAssignment::class, ShiftTemplate::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun shiftDao(): ShiftDao
    abstract fun shiftAssignmentDao(): ShiftAssignmentDao
    abstract fun shiftTemplateDao(): ShiftTemplateDao
}

