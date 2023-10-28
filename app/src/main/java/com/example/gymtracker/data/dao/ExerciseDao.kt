package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.gymtracker.data.model.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercise")
    fun observeExercises(): Flow<List<ExerciseEntity>>
    @Upsert
    suspend fun upsertExercise(exercise: ExerciseEntity): Long

    @Query("DELETE FROM exercise WHERE exercise.id LIKE :exerciseId")
    suspend fun deleteExercise(exerciseId: Long)

}
