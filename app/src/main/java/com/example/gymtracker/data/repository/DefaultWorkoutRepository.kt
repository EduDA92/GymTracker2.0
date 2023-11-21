package com.example.gymtracker.data.repository

import com.example.gymtracker.data.dao.WorkoutDao
import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import com.example.gymtracker.data.model.asEntity
import com.example.gymtracker.data.model.toExternalModel
import com.example.gymtracker.ui.model.Workout
import com.example.gymtracker.ui.model.WorkoutAndExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject


class DefaultWorkoutRepository @Inject constructor(private val workoutDao: WorkoutDao) :
    WorkoutRepository {

    override fun observeWorkouts(): Flow<List<Workout>> =
        workoutDao.observeWorkouts().map {
            it.toExternalModel()
        }

    override fun observeFullWorkout(workoutDate: LocalDate): Flow<WorkoutAndExercises?> =
        workoutDao.observeFullWorkout(workoutDate).map {
            it?.toExternalModel()
        }

    override fun observeFullWorkoutFromId(workoutId: Long): Flow<WorkoutAndExercises> =
        workoutDao.observeFullWorkoutFromId(workoutId).map {
            it.toExternalModel()
        }

    override suspend fun upsertWorkout(workout: Workout): Long =
        workoutDao.upsertWorkout(workout.asEntity())

    override suspend fun deleteWorkout(workoutId: Long) =
        workoutDao.deleteWorkout(workoutId)

    override suspend fun upsertWorkoutExerciseCrossRef(workoutExerciseCrossRef: WorkoutExerciseCrossRef) =
        workoutDao.upsertWorkoutExerciseCrossRef(workoutExerciseCrossRef)

    override suspend fun updateCompleteWorkout(workoutId: Long, isWorkoutCompleted: Boolean) =
        workoutDao.updateCompleteWorkout(workoutId, isWorkoutCompleted)

    override suspend fun updateWorkoutDuration(workoutId: Long, workoutDuration: Long) =
        workoutDao.updateWorkoutDuration(workoutId, workoutDuration)

    override suspend fun updateWorkoutName(workoutId: Long, workoutName: String) =
        workoutDao.updateWorkoutName(workoutId, workoutName)
}
