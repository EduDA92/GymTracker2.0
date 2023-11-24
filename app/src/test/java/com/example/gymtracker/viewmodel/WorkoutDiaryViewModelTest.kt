package com.example.gymtracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.gymtracker.testdoubles.repository.TestWorkoutRepository
import com.example.gymtracker.ui.model.ExerciseAndSets
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.model.WorkoutAndExercises
import com.example.gymtracker.ui.workoutDiary.WorkoutDiaryUiState
import com.example.gymtracker.ui.workoutDiary.WorkoutDiaryViewModel
import com.example.gymtracker.utils.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class WorkoutDiaryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: TestWorkoutRepository
    private lateinit var viewModel: WorkoutDiaryViewModel

    @Before
    fun init() {
        repository = TestWorkoutRepository()
        viewModel = WorkoutDiaryViewModel(repository, SavedStateHandle(mapOf("workoutId" to 1L)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun workoutDiaryViewModel_workoutDiaryUiStateValue_filtersSetsByDateAndDataIsCorrect() = runTest{

        val filteredList = exerciseAndSets.map {
             it.copy(
                 sets = it.sets.filter { it.date == LocalDate.now() }
             )
        }

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)){viewModel.workoutDiaryUiState.collect()}

        repository.emitWorkoutAndExercisesFromId(fullWorkout)

        val state = viewModel.workoutDiaryUiState.value

        assertEquals(filteredList, (state as WorkoutDiaryUiState.Success).diary.exercisesWithReps)

        collectJob.cancel()
    }


    private val exerciseAndSets = listOf(
        ExerciseAndSets(
            exerciseId = 1,
            exerciseName = "DeadLift",
            exerciseType = ExerciseType.Legs,
            sets = listOf(
                ExerciseSet(
                    id = 1,
                    exerciseId = 1,
                    reps = 10,
                    weight = 80.5f,
                    date = LocalDate.now(),
                    isCompleted = true
                ),
                ExerciseSet(
                    id = 2,
                    exerciseId = 1,
                    reps = 8,
                    weight = 88.5f,
                    date = LocalDate.now(),
                    isCompleted = true
                ),
                ExerciseSet(
                    id = 3,
                    exerciseId = 1,
                    reps = 4,
                    weight = 90.5f,
                    date = LocalDate.now(),
                    isCompleted = true
                ),
                ExerciseSet(
                    id = 10,
                    exerciseId = 1,
                    reps = 4,
                    weight = 121.5f,
                    date = LocalDate.now().plusDays(1),
                    isCompleted = true
                )
            )
        ),
        ExerciseAndSets(
            exerciseId = 2,
            exerciseName = "BenchPress",
            exerciseType = ExerciseType.Chest,
            sets = listOf(
                ExerciseSet(
                    id = 4,
                    exerciseId = 2,
                    reps = 10,
                    weight = 80.5f,
                    date = LocalDate.now(),
                    isCompleted = true
                ),
                ExerciseSet(
                    id = 5,
                    exerciseId = 2,
                    reps = 8,
                    weight = 88.5f,
                    date = LocalDate.now(),
                    isCompleted = true
                ),
                ExerciseSet(
                    id = 6,
                    exerciseId = 2,
                    reps = 4,
                    weight = 90.5f,
                    date = LocalDate.now(),
                    isCompleted = true
                ),
                ExerciseSet(
                    id = 11,
                    exerciseId = 1,
                    reps = 4,
                    weight = 121.5f,
                    date = LocalDate.now().plusDays(1),
                    isCompleted = true
                )
            )
        ),
        ExerciseAndSets(
            exerciseId = 1,
            exerciseName = "DeadLift",
            exerciseType = ExerciseType.Legs,
            sets = listOf(
                ExerciseSet(
                    id = 1,
                    exerciseId = 1,
                    reps = 10,
                    weight = 80.5f,
                    date = LocalDate.now(),
                    isCompleted = true
                ),
                ExerciseSet(
                    id = 2,
                    exerciseId = 1,
                    reps = 8,
                    weight = 88.5f,
                    date = LocalDate.now(),
                    isCompleted = true
                ),
                ExerciseSet(
                    id = 3,
                    exerciseId = 1,
                    reps = 4,
                    weight = 90.5f,
                    date = LocalDate.now(),
                    isCompleted = true
                )
                ,
                ExerciseSet(
                    id = 12,
                    exerciseId = 1,
                    reps = 4,
                    weight = 90.5f,
                    date = LocalDate.now().plusDays(1),
                    isCompleted = true
                )
            )
        ),
        ExerciseAndSets(
            exerciseId = 1,
            exerciseName = "EmptyExercise",
            exerciseType = ExerciseType.Legs,
            sets = listOf(
                ExerciseSet(
                    id = 1,
                    exerciseId = 1,
                    reps = 10,
                    weight = 80.5f,
                    date = LocalDate.now().plusDays(1),
                    isCompleted = true
                ),
                ExerciseSet(
                    id = 2,
                    exerciseId = 1,
                    reps = 8,
                    weight = 88.5f,
                    date = LocalDate.now().plusDays(1),
                    isCompleted = true
                ),
                ExerciseSet(
                    id = 3,
                    exerciseId = 1,
                    reps = 4,
                    weight = 90.5f,
                    date = LocalDate.now().plusDays(1),
                    isCompleted = true
                )
                ,
                ExerciseSet(
                    id = 12,
                    exerciseId = 1,
                    reps = 4,
                    weight = 90.5f,
                    date = LocalDate.now().plusDays(1),
                    isCompleted = true
                )
            )
        )
    )

    private val fullWorkout = WorkoutAndExercises(
        workoutId = 1,
        workoutName = "Push",
        workoutDate = LocalDate.now(),
        workoutDuration = 1000,
        workoutCompleted = true,
        exercisesAndSets = exerciseAndSets
    )

}