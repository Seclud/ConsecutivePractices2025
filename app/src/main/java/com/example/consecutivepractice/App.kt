package com.example.consecutivepractice

import android.app.Application
import com.example.consecutivepractice.di.appModule
import com.example.profile.di.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, profileModule))
        }
    }
}
