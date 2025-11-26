package com.naveen.schedittestapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shift_templates")
data class ShiftTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val location: String,
    val startHour: Int, // 0-23
    val startMinute: Int, // 0-59
    val durationHours: Int, // Duration in hours
    val requiredSkills: List<String>, // Stored as comma-separated string in DB
    val minStaffing: Int = 1,
    val dayOfWeek: Int? = null // 1-7 (Monday-Sunday), null for daily
) {
    fun getEndHour(): Int {
        val totalMinutes = startHour * 60 + startMinute + (durationHours * 60)
        return (totalMinutes / 60) % 24
    }
    
    fun getEndMinute(): Int {
        val totalMinutes = startHour * 60 + startMinute + (durationHours * 60)
        return totalMinutes % 60
    }
}

