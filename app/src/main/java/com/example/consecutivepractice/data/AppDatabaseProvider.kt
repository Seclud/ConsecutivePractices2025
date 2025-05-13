package com.example.consecutivepractice.data

import android.content.Context
import com.example.consecutivepractice.data.room.AppDatabase

class AppDatabaseProvider(private val context: Context) {
    fun getDatabase(): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
}
