package com.example.gymtracker.data.repository

import com.example.gymtracker.data.dao.ExerciseSetDao
import com.example.gymtracker.data.model.asEntity
import com.example.gymtracker.data.model.toExternalModel
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class DefaultExerciseSetRepository @Inject constructor(private val exerciseSetDao: ExerciseSetDao) :
    ExerciseSetRepository {

    override fun observeExerciseSets(): Flow<List<ExerciseSet>> =
        exerciseSetDao.observeExerciseSets().map { it.toExternalModel() }

    override fun observeExerciseSetHistory(exerciseId: Long): Flow<List<HistoryItem>> =
        exerciseSetDao.getExerciseSetHistory(exerciseId).map { it.toExternalModel() }

    override suspend fun upsertExerciseSet(exerciseSet: ExerciseSet): Long =
        exerciseSetDao.upsertExerciseSet(exerciseSet.asEntity())

    override suspend fun deleteExerciseSet(exerciseSetId: Long) =
        exerciseSetDao.deleteExerciseSet(exerciseSetId)

    override suspend fun updateCompleteExerciseSet(
        exerciseSetId: Long,
        isExerciseSetCompleted: Boolean
    ) = exerciseSetDao.updateCompleteExerciseSet(exerciseSetId, isExerciseSetCompleted)

    override suspend fun updateExerciseSetData(
        exerciseSetId: Long,
        setReps: Int,
        setWeight: Float
    ) = exerciseSetDao.updateExerciseSetData(exerciseSetId, setReps, setWeight)

    override suspend fun getExerciseSetIdList(exerciseId: Long, date: LocalDate): List<Long> =
        exerciseSetDao.getExerciseSetIdList(exerciseId, date)

    override suspend fun deleteExerciseSets(idList: List<Long>) =
        exerciseSetDao.deleteExerciseSets(idList)
}
