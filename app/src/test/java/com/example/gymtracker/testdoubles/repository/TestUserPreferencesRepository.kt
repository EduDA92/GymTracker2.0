package com.example.gymtracker.testdoubles.repository

import com.example.gymtracker.data.repository.UserPreferencesRepository
import com.example.gymtracker.ui.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestUserPreferencesRepository: UserPreferencesRepository {

    override val userData: Flow<UserPreferences> = flow{}

    override suspend fun updateUserFirstTimeLog() {
        /*noOP*/
    }

    override suspend fun updateSavedTimerDuration(duration: Long) {
        /*NOop*/
    }
}