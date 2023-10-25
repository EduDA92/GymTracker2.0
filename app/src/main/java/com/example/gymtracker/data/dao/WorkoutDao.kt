package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.gymtracker.data.model.WorkoutEntity
import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query(value = "SELECT * FROM workout")
    fun observeWorkouts(): Flow<List<WorkoutEntity>>

    @Upsert
    suspend fun upsertWorkout(workout: WorkoutEntity)

    @Query(value = "DELETE FROM workout WHERE workout.id LIKE :id")
    suspend fun deleteWorkout(id: Long)

    @Upsert
    suspend fun upsertWorkoutExerciseCrossRef(workoutExerciseCrossRef: WorkoutExerciseCrossRef)
}