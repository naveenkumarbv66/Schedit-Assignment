package com.naveen.schedittestapp.data.dao

import androidx.room.*
import com.naveen.schedittestapp.data.model.ShiftTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftTemplateDao {
    @Query("SELECT * FROM shift_templates ORDER BY name")
    fun getAllTemplates(): Flow<List<ShiftTemplate>>

    @Query("SELECT * FROM shift_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): ShiftTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: ShiftTemplate): Long

    @Update
    suspend fun updateTemplate(template: ShiftTemplate)

    @Delete
    suspend fun deleteTemplate(template: ShiftTemplate)
}

