package com.example.gymtracker.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.example.gymtracker.data.repository.DefaultUserPreferencesRepository
import com.example.gymtracker.ui.model.UserPreferences
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class UserPreferencesRepositoryTest {

    var context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userPreferencesRepository: DefaultUserPreferencesRepository

    @Before
    fun setup(){

        context = ApplicationProvider.getApplicationContext()

        dataStore = PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("TEST_DATASTORE")
        }

        userPreferencesRepository = DefaultUserPreferencesRepository(dataStore)

    }

    @Test
    fun defaultUserPreferencesRepository_updateUserFirstTimeLog_worksAsExpected() = runTest {
        // First check default state
        var state = userPreferencesRepository.userData.first()

        assertEquals(expectedUserPrefs, state)

        userPreferencesRepository.updateUserFirstTimeLog()

        state = userPreferencesRepository.userData.first()

        assertEquals(expectedUserPrefs.copy(firstTimeLog = false), state)

    }


    private val expectedUserPrefs = UserPreferences(
        firstTimeLog = true
    )

}