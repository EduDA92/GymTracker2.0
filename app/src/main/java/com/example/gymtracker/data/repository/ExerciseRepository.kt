package com.example.gymtracker.data.repository

import com.example.gymtracker.ui.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {

    fun observeExercises(): Flow<List<Exercise>>

    suspend fun upsertExercise(exercise: Exercise): Long

    suspend fun deleteExercise(exerciseId: Long)
}
