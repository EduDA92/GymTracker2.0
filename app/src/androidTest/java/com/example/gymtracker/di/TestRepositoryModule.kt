package com.example.gymtracker.di

import com.example.gymtracker.data.di.RepositoryModule
import com.example.gymtracker.data.repository.DefaultExerciseRepository
import com.example.gymtracker.data.repository.ExerciseRepository
import com.example.gymtracker.data.repository.WorkoutRepository
import com.example.gymtracker.data.repository.fakeRepository.FakeWorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
interface TestRepositoryModule {

    @Binds
    fun bindsFakeWorkoutRepository(
        workoutRepository: FakeWorkoutRepository
    ): WorkoutRepository

    //TODO change
    @Binds
    fun bindsExerciseRepository(
        exerciseRepository: DefaultExerciseRepository
    ): ExerciseRepository

}