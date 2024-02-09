package com.example.gymtracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.gymtracker.data.di.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import kotlin.random.Random


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class]
)
object TestDataStoreModule {

    @Singleton
    @Provides
    fun providesPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {

        val random = Random.nextInt()

        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("USER_PREFERENCES-$random") }
        )
    }


}