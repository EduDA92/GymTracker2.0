package com.example.gymtracker.data.di

import com.example.gymtracker.data.repository.DefaultExerciseRepository
import com.example.gymtracker.data.repository.DefaultExerciseSetRepository
import com.example.gymtracker.data.repository.DefaultTimerServiceRepository
import com.example.gymtracker.data.repository.DefaultUserPreferencesRepository
import com.example.gymtracker.data.repository.DefaultWeighsRepository
import com.example.gymtracker.data.repository.DefaultWorkoutRepository
import com.example.gymtracker.data.repository.ExerciseRepository
import com.example.gymtracker.data.repository.ExerciseSetRepository
import com.example.gymtracker.data.repository.TimerServiceRepository
import com.example.gymtracker.data.repository.UserPreferencesRepository
import com.example.gymtracker.data.repository.WeightsRepository
import com.example.gymtracker.data.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindsWorkoutRepository(
        workoutRepository: DefaultWorkoutRepository
    ): WorkoutRepository

    @Binds
    fun bindsExerciseRepository(
        exerciseRepository: DefaultExerciseRepository
    ): ExerciseRepository

    @Binds
    fun bindsExerciseSetRepository(
        exerciseSetRepository: DefaultExerciseSetRepository
    ): ExerciseSetRepository

    @Binds
    fun bindsWeighsRepository(
        weightsRepository: DefaultWeighsRepository
    ): WeightsRepository

    @Binds
    fun bindsUserPreferencesRepository(
        preferencesRepository: DefaultUserPreferencesRepository
    ): UserPreferencesRepository

    @Binds
    fun bindsTimerServiceRepository(
        timerServiceRepository: DefaultTimerServiceRepository
    ): TimerServiceRepository

}
