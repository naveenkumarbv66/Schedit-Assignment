package com.naveen.schedittestapp.data.dao

import androidx.room.*
import com.naveen.schedittestapp.data.model.Shift
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {
    @Query("SELECT * FROM shifts ORDER BY startTime")
    fun getAllShifts(): Flow<List<Shift>>

    @Query("SELECT * FROM shifts WHERE id = :id")
    suspend fun getShiftById(id: Long): Shift?

    @Query("SELECT * FROM shifts WHERE startTime >= :startTime AND startTime < :endTime ORDER BY startTime")
    fun getShiftsByDateRange(startTime: Long, endTime: Long): Flow<List<Shift>>

    @Query("SELECT * FROM shifts WHERE location LIKE '%' || :location || '%' ORDER BY startTime")
    fun getShiftsByLocation(location: String): Flow<List<Shift>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: Shift): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShifts(shifts: List<Shift>)

    @Update
    suspend fun updateShift(shift: Shift)

    @Delete
    suspend fun deleteShift(shift: Shift)
}

