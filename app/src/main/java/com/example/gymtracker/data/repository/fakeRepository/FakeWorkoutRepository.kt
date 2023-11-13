package com.example.gymtracker.data.repository.fakeRepository

import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import com.example.gymtracker.data.repository.WorkoutRepository
import com.example.gymtracker.ui.model.Workout
import com.example.gymtracker.ui.model.WorkoutAndExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

// TODO IMPLEMENT THIS
class FakeWorkoutRepository@Inject constructor(): WorkoutRepository {
    override fun observeWorkouts(): Flow<List<Workout>> = flow{
        emptyList<Workout>()
    }

    override fun observeFullWorkout(workoutDate: LocalDate): Flow<WorkoutAndExercises?> = flow{
        emit(null)
    }

    override suspend fun upsertWorkout(workout: Workout): Long {
        return 1
    }

    override suspend fun deleteWorkout(workoutId: Long) {
        /* TODO to be implemented */
    }

    override suspend fun upsertWorkoutExerciseCrossRef(workoutExerciseCrossRef: WorkoutExerciseCrossRef) {
        /* TODO to be implemented */
    }

    override suspend fun updateCompleteWorkout(workoutId: Long, isWorkoutCompleted: Boolean) {
        /* TODO to be implemented */
    }

    override suspend fun updateWorkoutDuration(workoutId: Long, workoutDuration: Long) {
        /* TODO to be implemented */
    }
}