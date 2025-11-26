package com.naveen.schedittestapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "shifts")
data class Shift(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val location: String,
    val startTime: Long, // Unix timestamp
    val endTime: Long, // Unix timestamp
    val requiredSkills: List<String>, // Stored as comma-separated string in DB
    val minStaffing: Int = 1
) {
    fun getDurationHours(): Double {
        return (endTime - startTime) / 3600.0
    }
    
    fun overlapsWith(other: Shift): Boolean {
        return startTime < other.endTime && endTime > other.startTime
    }
}

