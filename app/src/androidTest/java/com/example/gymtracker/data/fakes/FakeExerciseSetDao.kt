package com.example.gymtracker.data.fakes

import com.example.gymtracker.data.dao.ExerciseSetDao
import com.example.gymtracker.data.model.ExerciseSetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeExerciseSetDao(exerciseSetList: List<ExerciseSetEntity>): ExerciseSetDao {

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
}
