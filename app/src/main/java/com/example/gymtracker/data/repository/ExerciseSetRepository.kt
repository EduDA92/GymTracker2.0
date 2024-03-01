package com.example.gymtracker.data.repository


import com.example.gymtracker.data.model.ExerciseSetHistoryItem
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.HistoryItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ExerciseSetRepository {

    fun observeExerciseSets(): Flow<List<ExerciseSet>>

    fun observeExerciseSetHistory(exerciseId: Long): Flow<List<HistoryItem>>

    suspend fun upsertExerciseSet(exerciseSet: ExerciseSet): Long

    suspend fun deleteExerciseSet(exerciseSetId: Long)

    suspend fun updateCompleteExerciseSet(exerciseSetId: Long, isExerciseSetCompleted: Boolean)

    suspend fun updateExerciseSetData(exerciseSetId: Long, setReps: Int, setWeight: Float)

    suspend fun getExerciseSetIdList(exerciseId: Long, date: LocalDate): List<Long>

    suspend fun deleteExerciseSets(idList: List<Long>)
}
