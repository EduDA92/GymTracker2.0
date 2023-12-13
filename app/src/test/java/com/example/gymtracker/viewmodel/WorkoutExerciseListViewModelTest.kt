package com.example.gymtracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.gymtracker.testdoubles.repository.TestExerciseRepository
import com.example.gymtracker.testdoubles.repository.TestWorkoutRepository
import com.example.gymtracker.ui.model.Exercise
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.workoutExerciseList.ExerciseState
import com.example.gymtracker.ui.workoutExerciseList.WorkoutExerciseListUiState
import com.example.gymtracker.ui.workoutExerciseList.WorkoutExerciseListViewModel
import com.example.gymtracker.utils.MainDispatcherRule
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class WorkoutExerciseListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var workoutRepository: TestWorkoutRepository
    private lateinit var exerciseRepository: TestExerciseRepository
    private lateinit var viewModel: WorkoutExerciseListViewModel

    @Before
    fun init() {

        workoutRepository = TestWorkoutRepository()
        exerciseRepository = TestExerciseRepository()
        viewModel = WorkoutExerciseListViewModel(
            workoutRepository = workoutRepository,
            exerciseRepository = exerciseRepository,
            savedStateHandle = SavedStateHandle(mapOf("workoutId" to 1L))
        )
    }

    /* Basic test to check if the state responds correctly to filter updates */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun workoutExerciseListViewModel_WorkoutExerciseListUiState_getsUpdatedAccordingly() =
        runTest {

            val collectJob =
                launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.workoutExerciseListScreenState.collect() }

            exerciseRepository.emitExercises(exerciseList)

            // First check unfiltered list
            var state = viewModel.workoutExerciseListScreenState.value

            assertEquals(exerciseStateList, (state as WorkoutExerciseListUiState.Success).state.exerciseList)

            // Check state filtering by name
            viewModel.updateSearchedExerciseName("exercise1")

            state = viewModel.workoutExerciseListScreenState.value

            assertEquals(
                exerciseStateList.filter { it.exercise.name.contains("exercise1") },
                (state as WorkoutExerciseListUiState.Success).state.exerciseList
            )
            assertEquals("exercise1", state.state.exerciseNameFilter)

            // Check State filtering by type
            viewModel.updateSearchedExerciseName("")
            viewModel.updateExerciseTypeFilter(ExerciseType.Arms)

            state = viewModel.workoutExerciseListScreenState.value

            assertEquals(exerciseStateList.filter { it.exercise.type == ExerciseType.Arms }, (state as WorkoutExerciseListUiState.Success).state.exerciseList)
            assertEquals(ExerciseType.Arms.name, state.state.exerciseTypeFilter)

            // Check state filtering by name and type
            viewModel.updateSearchedExerciseName("exercise1")
            viewModel.updateExerciseTypeFilter(ExerciseType.Arms)

            state = viewModel.workoutExerciseListScreenState.value
            assertEquals(exerciseStateList.filter { it.exercise.type == ExerciseType.Arms && it.exercise.name.contains("exercise1")}, (state as WorkoutExerciseListUiState.Success).state.exerciseList)
            assertEquals(ExerciseType.Arms.name, state.state.exerciseTypeFilter)
            assertEquals("exercise1", state.state.exerciseNameFilter)

            collectJob.cancel()

        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun workoutExerciseListViewModel_checkingAndUncheckingExercises_updatesStateAccordingly() = runTest {

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.workoutExerciseListScreenState.collect() }

        exerciseRepository.emitExercises(exerciseList)

        // Mark some exercises as checked
        viewModel.updateExerciseToCheckedList(0)
        viewModel.updateExerciseToCheckedList(1)

        val state = viewModel.workoutExerciseListScreenState.value

        assertEquals(testExerciseStateList, (state as WorkoutExerciseListUiState.Success).state.exerciseList)


        collectJob.cancel()
    }

    val exerciseStateList = persistentListOf(
        ExerciseState(
            exercise =  Exercise(
                id = 0,
                name = "exercise1",
                type = ExerciseType.Arms
            ),
            isChecked = false
        ),
        ExerciseState(
            Exercise(
                id = 1,
                name = "exercise2",
                type = ExerciseType.Legs
            ),
            isChecked = false
        ),
        ExerciseState(
            Exercise(
                id = 2,
                name = "exercise2",
                type = ExerciseType.Chest
            ),
            isChecked = false
        ),
        ExerciseState(
            Exercise(
                id = 3,
                name = "exercise4",
                type = ExerciseType.Arms
            ),
            isChecked = false
        )
    )

    val testExerciseStateList = persistentListOf(
        ExerciseState(
            exercise =  Exercise(
                id = 0,
                name = "exercise1",
                type = ExerciseType.Arms
            ),
            isChecked = true
        ),
        ExerciseState(
            Exercise(
                id = 1,
                name = "exercise2",
                type = ExerciseType.Legs
            ),
            isChecked = true
        ),
        ExerciseState(
            Exercise(
                id = 2,
                name = "exercise2",
                type = ExerciseType.Chest
            ),
            isChecked = false
        ),
        ExerciseState(
            Exercise(
                id = 3,
                name = "exercise4",
                type = ExerciseType.Arms
            ),
            isChecked = false
        )
    )

    val exerciseList = persistentListOf(
        Exercise(
            id = 0,
            name = "exercise1",
            type = ExerciseType.Arms
        ),
        Exercise(
            id = 1,
            name = "exercise2",
            type = ExerciseType.Legs
        ),
        Exercise(
            id = 2,
            name = "exercise2",
            type = ExerciseType.Chest
        ),
        Exercise(
            id = 3,
            name = "exercise4",
            type = ExerciseType.Arms
        )
    )

}