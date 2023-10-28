package com.example.gymtracker.data.fakes

import com.example.gymtracker.data.dao.ExerciseDao
import com.example.gymtracker.data.model.ExerciseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeExerciseDao(exerciseList: List<ExerciseEntity>): ExerciseDao {

    private val _exercises = exerciseList.toMutableList()
    private val exercisesStream = MutableStateFlow(_exercises)
    override fun observeExercises(): Flow<List<ExerciseEntity>> = exercisesStream

    override suspend fun upsertExercise(exercise: ExerciseEntity): Long {
        _exercises.removeIf{it.id == exercise.id}
        _exercises.add(exercise)
        exercisesStream.emit(_exercises)

        return exercise.id
    }

    override suspend fun deleteExercise(exerciseId: Long) {
        _exercises.removeIf { it.id == exerciseId }
        exercisesStream.emit(_exercises)
    }
}
