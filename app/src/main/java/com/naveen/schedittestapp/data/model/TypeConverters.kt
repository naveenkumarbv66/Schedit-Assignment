package com.naveen.schedittestapp.data.model

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromEmploymentType(value: EmploymentType): String {
        return value.name
    }

    @TypeConverter
    fun toEmploymentType(value: String): EmploymentType {
        return EmploymentType.valueOf(value)
    }
}

