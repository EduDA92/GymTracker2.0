package com.example.gymtracker.data.repository

import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import com.example.gymtracker.ui.model.Workout
import com.example.gymtracker.ui.model.WorkoutAndExercises
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface WorkoutRepository {

    fun observeWorkouts(): Flow<List<Workout>>

    fun observeFullWorkout(workoutDate: LocalDate): Flow<WorkoutAndExercises?>

    fun observeFullWorkoutFromId(workoutId: Long): Flow<WorkoutAndExercises>

    suspend fun getFullWorkout(workoutDate: LocalDate): WorkoutAndExercises?
    suspend fun upsertWorkout(workout: Workout): Long

    suspend fun deleteWorkout(workoutId: Long)

    suspend fun upsertWorkoutExerciseCrossRef(workoutExerciseCrossRef: WorkoutExerciseCrossRef)

    suspend fun deleteWorkoutExerciseCrossRef(workoutId: Long, exerciseId: Long)

    suspend fun updateCompleteWorkout(workoutId: Long, isWorkoutCompleted: Boolean)

    suspend fun updateWorkoutDuration(workoutId: Long, workoutDuration: Long)

    suspend fun updateWorkoutName(workoutId: Long, workoutName: String)

}
