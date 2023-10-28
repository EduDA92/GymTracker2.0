package com.example.gymtracker.data.repository

import com.example.gymtracker.data.dao.ExerciseDao
import com.example.gymtracker.data.model.asEntity
import com.example.gymtracker.data.model.toExternalModel
import com.example.gymtracker.ui.model.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultExerciseRepository @Inject constructor(private val exerciseDao: ExerciseDao) :
    ExerciseRepository {

    override fun observeExercises(): Flow<List<Exercise>> =
        exerciseDao.observeExercises().map { it.toExternalModel() }

    override suspend fun upsertExercise(exercise: Exercise): Long =
        exerciseDao.upsertExercise(exercise.asEntity())

    override suspend fun deleteExercise(exerciseId: Long) =
        exerciseDao.deleteExercise(exerciseId)
}
