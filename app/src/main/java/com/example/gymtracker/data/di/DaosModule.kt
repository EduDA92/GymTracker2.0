package com.example.gymtracker.data.di

import com.example.gymtracker.data.GymTrackerDatabase
import com.example.gymtracker.data.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesWorkoutDao(
        database: GymTrackerDatabase
    ): WorkoutDao = database.workoutDao()

}