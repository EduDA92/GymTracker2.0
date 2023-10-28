package com.example.gymtracker.data.repository

import com.example.gymtracker.data.dao.ExerciseSetDao
import com.example.gymtracker.data.model.asEntity
import com.example.gymtracker.data.model.toExternalModel
import com.example.gymtracker.ui.model.ExerciseSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultExerciseSetRepository @Inject constructor(private val exerciseSetDao: ExerciseSetDao):
    ExerciseSetRepository {

    override fun observeExerciseSets(): Flow<List<ExerciseSet>> =
        exerciseSetDao.observeExerciseSets().map {it.toExternalModel() }

    override suspend fun upsertExerciseSet(exerciseSet: ExerciseSet): Long =
        exerciseSetDao.upsertExerciseSet(exerciseSet.asEntity())

    override suspend fun deleteExerciseSet(exerciseSetId: Long) =
        exerciseSetDao.deleteExerciseSet(exerciseSetId)

    override suspend fun updateCompleteExerciseSet(
        exerciseSetId: Long,
        isExerciseSetCompleted: Boolean
    ) = exerciseSetDao.updateCompleteExerciseSet(exerciseSetId, isExerciseSetCompleted)
}
