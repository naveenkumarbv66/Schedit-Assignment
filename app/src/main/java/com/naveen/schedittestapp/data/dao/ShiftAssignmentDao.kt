package com.naveen.schedittestapp.data.dao

import androidx.room.*
import com.naveen.schedittestapp.data.model.ShiftAssignment
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftAssignmentDao {
    @Query("SELECT * FROM shift_assignments")
    fun getAllAssignments(): Flow<List<ShiftAssignment>>

    @Query("SELECT * FROM shift_assignments WHERE employeeId = :employeeId")
    fun getAssignmentsByEmployee(employeeId: Long): Flow<List<ShiftAssignment>>

    @Query("SELECT * FROM shift_assignments WHERE shiftId = :shiftId")
    fun getAssignmentsByShift(shiftId: Long): Flow<List<ShiftAssignment>>

    @Query("SELECT * FROM shift_assignments WHERE employeeId = :employeeId AND shiftId = :shiftId LIMIT 1")
    suspend fun getAssignment(employeeId: Long, shiftId: Long): ShiftAssignment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: ShiftAssignment): Long

    @Delete
    suspend fun deleteAssignment(assignment: ShiftAssignment)

    @Query("DELETE FROM shift_assignments WHERE employeeId = :employeeId AND shiftId = :shiftId")
    suspend fun deleteAssignment(employeeId: Long, shiftId: Long)
}

