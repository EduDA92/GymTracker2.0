package com.example.gymtracker.data.fakes

import com.example.gymtracker.data.dao.WorkoutDao
import com.example.gymtracker.data.model.WorkoutEntity
import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import com.example.gymtracker.data.model.WorkoutWithExercisesAndSets
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

class FakeWorkoutDao(workouts: List<WorkoutEntity>, fullWorkout: WorkoutWithExercisesAndSets): WorkoutDao {

    private val _workouts = workouts.toMutableList()
    private val workoutsStream = MutableStateFlow(_workouts.toList())

    private val fullWorkoutStream = MutableStateFlow(fullWorkout)
    override fun observeWorkouts(): Flow<List<WorkoutEntity>> = workoutsStream

    override fun observeFullWorkout(workoutDate: LocalDate): Flow<WorkoutWithExercisesAndSets> = fullWorkoutStream

    override suspend fun upsertWorkout(workout: WorkoutEntity): Long {
        _workouts.removeIf{it.id == workout.id}
        _workouts.add(workout)
        workoutsStream.emit(_workouts)

        return workout.id
    }

    override suspend fun deleteWorkout(workoutId: Long) {
        _workouts.removeIf { it.id == workoutId }
        workoutsStream.emit(_workouts)
    }

    override suspend fun updateCompleteWorkout(workoutId: Long, isWorkoutCompleted: Boolean) {
        /* No-op */
    }

    override suspend fun updateWorkoutDuration(workoutId: Long, workoutDuration: Long) {
        /* No-op */
    }

    override suspend fun upsertWorkoutExerciseCrossRef(workoutExerciseCrossRef: WorkoutExerciseCrossRef) {
       /* No-Op */
    }
}
