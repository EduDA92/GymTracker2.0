package com.example.gymtracker.testdoubles.repository

import com.example.gymtracker.data.repository.ExerciseSetRepository
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class TestExerciseSetRepository: ExerciseSetRepository {

    private val exerciseSetHistoryFlow = MutableSharedFlow<List<HistoryItem>>()

    override fun observeExerciseSets(): Flow<List<ExerciseSet>> = flow{
        emit(emptyList())
    }

    override fun observeExerciseSetHistory(exerciseId: Long): Flow<List<HistoryItem>> = exerciseSetHistoryFlow

    suspend fun emitExerciseSetHistory(exerciseHistory: List<HistoryItem>) = exerciseSetHistoryFlow.emit(exerciseHistory)

    override suspend fun upsertExerciseSet(exerciseSet: ExerciseSet): Long {
        return 1
    }

    override suspend fun deleteExerciseSet(exerciseSetId: Long) {
        /* no op */
    }

    override suspend fun updateCompleteExerciseSet(
        exerciseSetId: Long,
        isExerciseSetCompleted: Boolean
    ) {
        /* no op */
    }

    override suspend fun updateExerciseSetData(
        exerciseSetId: Long,
        setReps: Int,
        setWeight: Float
    ) {
        /* no op */
    }

    override suspend fun getExerciseSetIdList(exerciseId: Long, date: LocalDate): List<Long> {
        return emptyList()
    }

    override suspend fun deleteExerciseSets(idList: List<Long>) {
        /* no op */
    }
}