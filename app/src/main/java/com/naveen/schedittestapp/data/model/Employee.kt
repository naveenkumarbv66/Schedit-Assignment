package com.naveen.schedittestapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val skills: List<String>, // Stored as comma-separated string in DB
    val employmentType: EmploymentType,
    val maxWeeklyHours: Int = 40,
    val maxDailyHours: Int = 8
) {
    fun hasSkill(skill: String): Boolean = skills.contains(skill)
    fun hasAllSkills(requiredSkills: List<String>): Boolean = requiredSkills.all { skills.contains(it) }
}

enum class EmploymentType {
    FULL_TIME,
    PART_TIME
}

