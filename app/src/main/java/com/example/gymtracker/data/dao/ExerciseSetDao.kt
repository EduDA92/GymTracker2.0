package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.gymtracker.data.model.ExerciseSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseSetDao {

    @Query("SELECT * FROM exerciseSet")
    fun observeExerciseSets(): Flow<List<ExerciseSetEntity>>

    @Upsert
    suspend fun upsertExerciseSet(exerciseSet: ExerciseSetEntity): Long

    @Query("DELETE FROM exerciseSet WHERE exerciseSet.id LIKE :exerciseSetId")
    suspend fun deleteExerciseSet(exerciseSetId: Long)

    @Query("UPDATE exerciseSet SET isCompleted = :isExerciseSetCompleted WHERE exerciseId = :exerciseSetId")
    suspend fun updateCompleteExerciseSet(exerciseSetId: Long, isExerciseSetCompleted: Boolean)

}
