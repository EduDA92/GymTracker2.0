package com.example.gymtracker.data.repository

import com.example.gymtracker.data.fakes.FakeExerciseDao
import com.example.gymtracker.data.fakes.FakeExerciseSetDao
import com.example.gymtracker.data.fakes.FakeWorkoutDao
import com.example.gymtracker.data.model.ExerciseEntity
import com.example.gymtracker.data.model.ExerciseSetEntity
import com.example.gymtracker.data.model.ExerciseWithSets
import com.example.gymtracker.data.model.WorkoutEntity
import com.example.gymtracker.data.model.WorkoutWithExercisesAndSets
import com.example.gymtracker.data.model.toExternalModel
import com.example.gymtracker.ui.model.ExerciseType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class RepositoryTest {

    private lateinit var workoutDataSource: FakeWorkoutDao
    private lateinit var exerciseDataSource: FakeExerciseDao
    private lateinit var exerciseSetDatasource: FakeExerciseSetDao
    private lateinit var workoutRepository: DefaultWorkoutRepository
    private lateinit var exerciseRepository: DefaultExerciseRepository
    private lateinit var exerciseSetRepository: DefaultExerciseSetRepository

    @Before
    fun setup(){
        workoutDataSource = FakeWorkoutDao(listOf(testWorkoutEntity), testFullWorkout)
        exerciseDataSource = FakeExerciseDao(listOf(testExerciseEntity))
        exerciseSetDatasource = FakeExerciseSetDao(listOf(testExerciseSet))
        workoutRepository = DefaultWorkoutRepository(workoutDataSource)
        exerciseRepository = DefaultExerciseRepository(exerciseDataSource)
        exerciseSetRepository = DefaultExerciseSetRepository(exerciseSetDatasource)
    }

    @Test
    fun defaultWorkoutRepository_exposesWorkoutExternalModel() = runTest {
        val workouts = workoutRepository.observeWorkouts().first()

        assertEquals(testWorkoutEntity.toExternalModel(), workouts[0])
    }

    @Test
    fun defaultWorkoutRepository_exposesFullWorkoutExternalModel() = runTest {

        val fullWorkout = workoutRepository.observeFullWorkout(LocalDate.now()).first()

        assertEquals(testFullWorkout.toExternalModel(), fullWorkout)
    }

    @Test
    fun defaultExerciseRepository_exposesExerciseExternalModel() = runTest {

        val exerciseList = exerciseRepository.observeExercises().first()

        assertEquals(testExerciseEntity.toExternalModel(), exerciseList[0])
    }

    @Test
    fun defaultExerciseSetRepository_exposesExerciseSetExternalModel() = runTest {

        val exerciseSetList = exerciseSetRepository.observeExerciseSets().first()

        assertEquals(testExerciseSet.toExternalModel(), exerciseSetList[0])

    }


    private val testWorkoutEntity = WorkoutEntity(
        id = 1,
        name = "Push",
        date = LocalDate.now(),
        duration = 0,
        isCompleted = false
    )

    private val testExerciseEntity = ExerciseEntity(
        id = 1,
        name = "Deadlift",
        type = ExerciseType.Legs.name
    )

    private val testExerciseSet = ExerciseSetEntity(
        id = 1,
        exerciseId = 1,
        reps = 12,
        weight = 100f,
        date = LocalDate.now(),
        isCompleted = false
    )

    private val testFullWorkout = WorkoutWithExercisesAndSets(
        workout = testWorkoutEntity,
        exerciseWithSets = listOf(ExerciseWithSets(
            exercise = testExerciseEntity,
            sets = listOf(testExerciseSet, testExerciseSet.copy( id = 2, date = LocalDate.now().plusDays(2))),)
        )
    )
}
