package com.example.gymtracker.testdoubles.repository

import com.example.gymtracker.data.repository.ExerciseRepository
import com.example.gymtracker.ui.model.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class TestExerciseRepository : ExerciseRepository {

    private val exercisesFlow = MutableSharedFlow<List<Exercise>>()

    override fun observeExercises(): Flow<List<Exercise>> = exercisesFlow

    suspend fun emitExercises(exerciseList: List<Exercise>) = exercisesFlow.emit(exerciseList)


    override suspend fun upsertExercise(exercise: Exercise): Long {
        return 1
    }

    override suspend fun deleteExercise(exerciseId: Long) {
        /* no op */
    }
}