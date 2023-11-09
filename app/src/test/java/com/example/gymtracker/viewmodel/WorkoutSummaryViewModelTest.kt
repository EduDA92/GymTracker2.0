package com.example.gymtracker.viewmodel


import com.example.gymtracker.testdoubles.repository.TestWorkoutRepository
import com.example.gymtracker.ui.model.ExerciseAndSets
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.model.WorkoutAndExercises
import com.example.gymtracker.ui.workourSummary.WorkoutSummaryUiState
import com.example.gymtracker.ui.workourSummary.WorkoutSummaryViewModel
import com.example.gymtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertIs

class WorkoutSummaryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: TestWorkoutRepository
    private lateinit var viewModel: WorkoutSummaryViewModel
    @Before
    fun init() {
        repository = TestWorkoutRepository()
        viewModel = WorkoutSummaryViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun workoutSummaryViewModel_queryingDateWithWorkout_returnsSuccessState() = runTest {

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.workoutSummaryUiState.collect() }

        /* initial state */
        var currentState = viewModel.workoutSummaryUiState.value

        assertIs<WorkoutSummaryUiState.EmptyData>(currentState)

        /* Emitting a valid workout so state should be Success */
        repository.emitWorkoutAndExercises(fullWorkout)

        currentState = viewModel.workoutSummaryUiState.value

        assertIs<WorkoutSummaryUiState.Success>(currentState)

        collectJob.cancel()
    }

    @Test
    fun workoutSummaryViewModel_queryingDateWithoutWorkout_returnsEmptyState() = runTest {

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.workoutSummaryUiState.collect() }

        /* Checking initial state */
        var currentState = viewModel.workoutSummaryUiState.value

        assertIs<WorkoutSummaryUiState.EmptyData>(currentState)

        /* state for a null workout should be emptyState*/
        repository.emitWorkoutAndExercises(null)

        currentState = viewModel.workoutSummaryUiState.value

        assertIs<WorkoutSummaryUiState.EmptyData>(currentState)

        collectJob.cancel()

    }

    @Test
    fun workoutSummaryViewModel_workoutSummaryUiStateSuccessValue_returnsTopSet() = runTest {

        /* Actual topSet for the fake data given to the viewmodel */
        val topSet = Pair(90.5f, 4)

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.workoutSummaryUiState.collect() }

        /* emit fake full workout and check that the given topSet from the viewmodel matches the correct topSet */
        repository.emitWorkoutAndExercises(fullWorkout)

        val currentState = viewModel.workoutSummaryUiState.value as WorkoutSummaryUiState.Success

        assertEquals(topSet, currentState.workoutSummary.workoutSummary[0].topSet)

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
                )
            )
        )
    )

    private val fullWorkout = WorkoutAndExercises(
        workoutId = 1,
        workoutName = "Legs",
        workoutDate = LocalDate.now(),
        workoutDuration = 1000,
        workoutCompleted = true,
        exercisesAndSets = exerciseAndSets
    )

}