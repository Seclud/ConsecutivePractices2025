package com.example.consecutivepractice.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class AndroidContextProvider(private val application: Application) {
    fun getApplication(): Application = application

    fun getApplicationContext(): Context = application.applicationContext

    fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
        return application.getSharedPreferences(name, mode)
    }
}
