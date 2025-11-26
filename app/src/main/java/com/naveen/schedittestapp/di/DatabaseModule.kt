package com.naveen.schedittestapp.di

import android.content.Context
import androidx.room.Room
import com.naveen.schedittestapp.data.dao.EmployeeDao
import com.naveen.schedittestapp.data.dao.ShiftAssignmentDao
import com.naveen.schedittestapp.data.dao.ShiftDao
import com.naveen.schedittestapp.data.dao.ShiftTemplateDao
import com.naveen.schedittestapp.data.database.ScheduleDatabase
import com.naveen.schedittestapp.data.repository.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ScheduleDatabase {
        return Room.databaseBuilder(
            context,
            ScheduleDatabase::class.java,
            "schedule_database"
        )
        .fallbackToDestructiveMigration() // For development - in production, use proper migrations
        .build()
    }

    @Provides
    fun provideEmployeeDao(database: ScheduleDatabase): EmployeeDao {
        return database.employeeDao()
    }

    @Provides
    fun provideShiftDao(database: ScheduleDatabase): ShiftDao {
        return database.shiftDao()
    }

    @Provides
    fun provideShiftAssignmentDao(database: ScheduleDatabase): ShiftAssignmentDao {
        return database.shiftAssignmentDao()
    }

    @Provides
    fun provideShiftTemplateDao(database: ScheduleDatabase): ShiftTemplateDao {
        return database.shiftTemplateDao()
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(
        employeeDao: EmployeeDao,
        shiftDao: ShiftDao,
        shiftAssignmentDao: ShiftAssignmentDao,
        shiftTemplateDao: ShiftTemplateDao
    ): ScheduleRepository {
        return ScheduleRepository(employeeDao, shiftDao, shiftAssignmentDao, shiftTemplateDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object InitialDataModule {
    // InitialDataProvider will be provided by Hilt automatically via @Inject constructor
}

