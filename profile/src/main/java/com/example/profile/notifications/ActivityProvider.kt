package com.example.profile.notifications

import android.content.Context
import android.content.Intent

/**
 * Interface to decouple the profile module from the app's MainActivity
 */
interface ActivityProvider {
    /**
     * Creates an Intent to launch the main activity of the app
     */
    fun getMainActivityIntent(context: Context): Intent
}
