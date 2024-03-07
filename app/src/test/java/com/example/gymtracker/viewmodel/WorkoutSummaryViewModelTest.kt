package com.example.gymtracker.viewmodel


import com.example.gymtracker.data.repository.ExerciseSetRepository
import com.example.gymtracker.testdoubles.repository.TestExerciseSetRepository
import com.example.gymtracker.testdoubles.repository.TestWorkoutRepository
import com.example.gymtracker.ui.model.ExerciseAndSets
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.model.WorkoutAndExercises
import com.example.gymtracker.ui.workoutSummary.ExerciseSummary
import com.example.gymtracker.ui.workoutSummary.WorkoutSummaryUiState
import com.example.gymtracker.ui.workoutSummary.WorkoutSummaryViewModel
import com.example.gymtracker.utils.MainDispatcherRule
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
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
    private lateinit var exerciseSetRepository: TestExerciseSetRepository
    private lateinit var viewModel: WorkoutSummaryViewModel

    @Before
    fun init() {
        repository = TestWorkoutRepository()
        exerciseSetRepository = TestExerciseSetRepository()
        viewModel = WorkoutSummaryViewModel(repository, exerciseSetRepository)
    }

    /* When there is workout data for the current date the state should be WorkoutSummaryUiState.Success*/
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun workoutSummaryViewModel_queryingDateWithWorkout_returnsSuccessState() = runTest {

        val collectJob =
            launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.workoutSummaryUiState.collect() }

        /* initial state */
        var currentState = viewModel.workoutSummaryUiState.value

        assertIs<WorkoutSummaryUiState.Loading>(currentState)

        /* Emitting a valid workout so state should be Success */
        repository.emitWorkoutAndExercises(fullWorkout)

        currentState = viewModel.workoutSummaryUiState.value

        assertIs<WorkoutSummaryUiState.Success>(currentState)

        collectJob.cancel()
    }

    /* When there is no workout data for the current date the state should be WorkoutSummaryUiState.EmptyData*/
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun workoutSummaryViewModel_queryingDateWithoutWorkout_returnsEmptyState() = runTest {

        val collectJob =
            launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.workoutSummaryUiState.collect() }

        /* Checking initial state */
        var currentState = viewModel.workoutSummaryUiState.value

        assertIs<WorkoutSummaryUiState.Loading>(currentState)

        /* state for a null workout should be emptyState*/
        repository.emitWorkoutAndExercises(null)

        currentState = viewModel.workoutSummaryUiState.value

        assertIs<WorkoutSummaryUiState.EmptyData>(currentState)

        collectJob.cancel()

    }

    /* Ensuring that the topSet returned inside the state is correct, make sure that the sets are filtered too */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun workoutSummaryViewModel_workoutSummaryUiStateSuccessValue_returnsTopSet() = runTest {

        /* Actual topSet for the fake data given to the viewmodel */
        val topSet = Pair(90.5f, 4)

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.workoutSummaryUiState.collect() }

        /* emit fake full workout and check that the given topSet from the viewmodel matches the correct topSet */
        repository.emitWorkoutAndExercises(fullWorkout)

        val currentState = viewModel.workoutSummaryUiState.value as WorkoutSummaryUiState.Success

        assertEquals(topSet, currentState.workoutSummary.exercisesSummary[0].topSet)

        collectJob.cancel()
    }

    /* The current fullworkout contains all the sets done for each exercise no matter the date, so its
    * the task for the viewmodel to filter the sets by date, this test assures that the data is correctly
    * filtered */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun workoutSummaryViewModel_workoutSummaryUiStateSuccessValue_filtersByDate() = runTest {


        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.workoutSummaryUiState.collect() }

        repository.emitWorkoutAndExercises(fullWorkout)

        val currentState = viewModel.workoutSummaryUiState.value as WorkoutSummaryUiState.Success

        assertEquals(testExerciseSummary, currentState.workoutSummary.exercisesSummary[0])

        /* the exerciseSummary for EmptyExercise should be a summary with size 0 and Pair<0f,0> TopSet,
        * this exercise is empty because all of its sets are from a wrong date*/

        assertEquals(emptyTestSummary, currentState.workoutSummary.exercisesSummary[3])

        /* Assert that the state total reps and weight are the expected ones */

        assertEquals(testTotalRepsVolume, currentState.workoutSummary.workoutTotalRepsVolume)
        assertEquals(testTotalWeightVolume, currentState.workoutSummary.workoutTotalWeightVolume)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun workoutSummaryViewModel_workoutSummaryUiStateSuccessValue_returnsCorrectExerciseTypeDistribution() =
        runTest {

            val exerciseDistribution = persistentMapOf<ExerciseType, Int>(
                ExerciseType.Legs to 3,
                ExerciseType.Chest to 1
            )

            val collectJob =
                launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.workoutSummaryUiState.collect() }

            /* emit fake full workout and check that the given exerciseDistribution from the viewmodel
            matches the correct exerciseDistribution */
            repository.emitWorkoutAndExercises(fullWorkout)

            val currentState =
                viewModel.workoutSummaryUiState.value as WorkoutSummaryUiState.Success

            assertEquals(
                exerciseDistribution,
                currentState.workoutSummary.workoutExerciseDistribution
            )

            collectJob.cancel()

        }


    private val exerciseAndSets = persistentListOf(
        ExerciseAndSets(
            exerciseId = 1,
            exerciseName = "DeadLift",
            exerciseType = ExerciseType.Legs,
            sets = persistentListOf(
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
            sets = persistentListOf(
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
            sets = persistentListOf(
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
            sets = persistentListOf(
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
        workoutName = "Legs",
        workoutDate = LocalDate.now(),
        workoutDuration = 1000,
        workoutCompleted = true,
        exercisesAndSets = exerciseAndSets
    )

    /* Summary of the fake data with the sets filtered to check if the viewmodel filters the sets
     * by date correctly */
    private val testExerciseSummary = ExerciseSummary(
        name = "DeadLift",
        sets = 3,
        topSet = Pair(90.5f, 4)
    )

    /* Empty Summary with incorrect dates to check if the filter works correctly */
    private val emptyTestSummary = ExerciseSummary(
        name = "EmptyExercise",
        sets = 0,
        topSet = Pair(0f, 0)
    )

    private val testTotalRepsVolume = 66
    private val testTotalWeightVolume = 778.5f

}