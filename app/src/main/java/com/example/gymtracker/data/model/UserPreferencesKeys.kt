package com.example.gymtracker.data.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

object UserPreferencesKeys {

    val IS_FIRST_TIME_LOG = booleanPreferencesKey("first_time_log")
    val SAVED_TIMER_DURATION = longPreferencesKey("saved_timer_duration")

}