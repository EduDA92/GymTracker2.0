package com.example.gymtracker.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gymtracker.data.GymTrackerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesGymTrackerDatabase(
        @ApplicationContext context: Context
    ): GymTrackerDatabase = Room.databaseBuilder(
        context,
        GymTrackerDatabase::class.java,
        "GymTracker-Database"
    ).build()
}