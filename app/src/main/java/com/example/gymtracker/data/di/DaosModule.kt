package com.example.gymtracker.data.di

import com.example.gymtracker.data.GymTrackerDatabase
import com.example.gymtracker.data.dao.BarDao
import com.example.gymtracker.data.dao.ExerciseDao
import com.example.gymtracker.data.dao.ExerciseSetDao
import com.example.gymtracker.data.dao.PlateDao
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

    @Provides
    fun providesExerciseDao(
        database: GymTrackerDatabase
    ): ExerciseDao = database.exerciseDao()

    @Provides
    fun providesExerciseSetDao(
        database: GymTrackerDatabase
    ): ExerciseSetDao = database.exerciseSetDao()

    @Provides
    fun providesPlateDao(
        database: GymTrackerDatabase
    ): PlateDao = database.plateDao()

    @Provides
    fun providesBarDao(
        database: GymTrackerDatabase
    ): BarDao = database.barDao()

}