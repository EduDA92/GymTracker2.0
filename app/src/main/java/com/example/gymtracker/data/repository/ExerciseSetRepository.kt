package com.example.gymtracker.data.repository


import com.example.gymtracker.ui.model.ExerciseSet
import kotlinx.coroutines.flow.Flow

interface ExerciseSetRepository {

    fun observeExerciseSets(): Flow<List<ExerciseSet>>

    suspend fun upsertExerciseSet(exerciseSet: ExerciseSet): Long

    suspend fun deleteExerciseSet(exerciseSetId: Long)

    suspend fun updateCompleteExerciseSet(exerciseSetId: Long, isExerciseSetCompleted: Boolean)
}
