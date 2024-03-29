package com.example.gymtracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.gymtracker.data.model.UserPreferencesKeys
import com.example.gymtracker.ui.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultUserPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    UserPreferencesRepository {

    override val userData: Flow<UserPreferences> = dataStore.data.map { preferences ->

        val firstTimeLog = preferences[UserPreferencesKeys.IS_FIRST_TIME_LOG] ?: true
        val savedTimerDuration = preferences[UserPreferencesKeys.SAVED_TIMER_DURATION] ?: 0L

        UserPreferences(firstTimeLog, savedTimerDuration)
    }

    override suspend fun updateUserFirstTimeLog() {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_FIRST_TIME_LOG] = false
        }
    }

    override suspend fun updateSavedTimerDuration(duration: Long) {
        dataStore.edit {preferences ->
             preferences[UserPreferencesKeys.SAVED_TIMER_DURATION] = duration
        }
    }
}