package com.example.gymtracker.testdoubles.dao

import com.example.gymtracker.data.dao.ExerciseSetDao
import com.example.gymtracker.data.model.ExerciseSetEntity
import com.example.gymtracker.data.model.ExerciseSetHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class TestExerciseSetDao(exerciseSetList: List<ExerciseSetEntity>): ExerciseSetDao {

    private val _exerciseSetList = exerciseSetList.toMutableList()
    private val exercisesStream = MutableStateFlow(_exerciseSetList)
    override fun observeExerciseSets(): Flow<List<ExerciseSetEntity>> =
        exercisesStream

    override suspend fun upsertExerciseSet(exerciseSet: ExerciseSetEntity): Long {
        _exerciseSetList.removeIf{it.id == exerciseSet.id}
        _exerciseSetList.add(exerciseSet)
        exercisesStream.emit(_exerciseSetList)

        return exerciseSet.id
    }

    override suspend fun deleteExerciseSet(exerciseSetId: Long) {
        _exerciseSetList.removeIf { it.id == exerciseSetId }
        exercisesStream.emit(_exerciseSetList)
    }

    override suspend fun updateCompleteExerciseSet(
        exerciseSetId: Long,
        isExerciseSetCompleted: Boolean
    ) {
        /* No-op */
    }

    override suspend fun updateExerciseSetData(
        exerciseSetId: Long,
        setReps: Int,
        setWeight: Float
    ) {
        /* No-op */
    }

    override suspend fun getExerciseSetIdList(exerciseId: Long, date: LocalDate): List<Long> {
        return emptyList()
    }

    override fun getExerciseSetHistory(exerciseId: Long): Flow<List<ExerciseSetHistoryItem>> = flow{
        emptyList<ExerciseSetHistoryItem>()
    }

    override suspend fun deleteExerciseSets(idList: List<Long>) {
        /* No-op */
    }
}
