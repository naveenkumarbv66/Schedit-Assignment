package com.naveen.schedittestapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.naveen.schedittestapp.data.dao.EmployeeDao
import com.naveen.schedittestapp.data.dao.ShiftAssignmentDao
import com.naveen.schedittestapp.data.dao.ShiftDao
import com.naveen.schedittestapp.data.model.Converters
import com.naveen.schedittestapp.data.model.Employee
import com.naveen.schedittestapp.data.model.Shift
import com.naveen.schedittestapp.data.model.ShiftAssignment

@Database(
    entities = [Employee::class, Shift::class, ShiftAssignment::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun shiftDao(): ShiftDao
    abstract fun shiftAssignmentDao(): ShiftAssignmentDao
}

