package com.example.gymtracker.data.repository

import com.example.gymtracker.ui.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val userData: Flow<UserPreferences>

    suspend fun updateUserFirstTimeLog()
}