package com.example.gymtracker.testdoubles.repository

import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import com.example.gymtracker.data.repository.WorkoutRepository
import com.example.gymtracker.ui.model.Workout
import com.example.gymtracker.ui.model.WorkoutAndExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class TestWorkoutRepository : WorkoutRepository {

    private val workoutAndExercisesFlow = MutableSharedFlow<WorkoutAndExercises?>()

    override fun observeWorkouts(): Flow<List<Workout>> = flow {
        emit(emptyList())
    }

    override fun observeFullWorkout(workoutDate: LocalDate): Flow<WorkoutAndExercises?> =
        workoutAndExercisesFlow

    /* Helper method to control the workout returned by the observeFullWorkout */
    suspend fun emitWorkoutAndExercises(workoutAndExercises: WorkoutAndExercises?) =
        workoutAndExercisesFlow.emit(workoutAndExercises)

    override suspend fun upsertWorkout(workout: Workout): Long {
        return 0
    }

    override suspend fun deleteWorkout(workoutId: Long) {
        /* nothing here */
    }

    override suspend fun upsertWorkoutExerciseCrossRef(workoutExerciseCrossRef: WorkoutExerciseCrossRef) {
        /* nothing here */
    }

    override suspend fun updateCompleteWorkout(workoutId: Long, isWorkoutCompleted: Boolean) {
        /* nothing here */
    }

    override suspend fun updateWorkoutDuration(workoutId: Long, workoutDuration: Long) {
        /* nothing here */
    }

}