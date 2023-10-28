package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.gymtracker.data.model.WorkoutEntity
import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import com.example.gymtracker.data.model.WorkoutWithExercisesAndSets
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workout")
    fun observeWorkouts(): Flow<List<WorkoutEntity>>

    @Transaction
    @Query("SELECT * FROM workout WHERE workout.date LIKE :workoutDate")
    fun observeFullWorkout(workoutDate: LocalDate): Flow<WorkoutWithExercisesAndSets>

    @Upsert
    suspend fun upsertWorkout(workout: WorkoutEntity): Long

    @Query("DELETE FROM workout WHERE workout.id LIKE :workoutId")
    suspend fun deleteWorkout(workoutId: Long)

    @Query("UPDATE workout SET isCompleted = :isWorkoutCompleted WHERE workout.id = :workoutId")
    suspend fun updateCompleteWorkout(workoutId: Long, isWorkoutCompleted: Boolean)

    @Query("Update workout SET duration = :workoutDuration WHERE workout.id = :workoutId")
    suspend fun updateWorkoutDuration(workoutId: Long, workoutDuration: Long)

    @Upsert
    suspend fun upsertWorkoutExerciseCrossRef(workoutExerciseCrossRef: WorkoutExerciseCrossRef)
}
