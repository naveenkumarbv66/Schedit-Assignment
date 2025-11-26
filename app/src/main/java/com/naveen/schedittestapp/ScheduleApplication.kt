package com.naveen.schedittestapp

import android.app.Application
import com.naveen.schedittestapp.data.InitialDataProvider
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class ScheduleApplication : Application() {
    
    @Inject
    lateinit var initialDataProvider: InitialDataProvider
    
    // Application-scoped CoroutineScope
    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        // Initialize sample data
        initialDataProvider.initializeData(applicationScope)
    }
}

