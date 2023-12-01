package com.example.gymtracker.di

import android.content.Context
import androidx.room.Room
import com.example.gymtracker.data.GymTrackerDatabase
import com.example.gymtracker.data.di.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun providesTestGymTrackerDatabase(
        @ApplicationContext context: Context
    ): GymTrackerDatabase = Room.inMemoryDatabaseBuilder(
        context,
        GymTrackerDatabase::class.java
    ).build()

}