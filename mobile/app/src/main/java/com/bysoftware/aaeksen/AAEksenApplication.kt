package com.bysoftware.aaeksen

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AAEksenApplication : Application() {
    
    companion object {
        private var instance: AAEksenApplication? = null
        
        fun getInstance(): AAEksenApplication {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}