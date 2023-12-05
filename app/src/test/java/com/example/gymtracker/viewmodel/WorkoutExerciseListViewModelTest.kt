package com.example.gymtracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.gymtracker.testdoubles.repository.TestExerciseRepository
import com.example.gymtracker.testdoubles.repository.TestWorkoutRepository
import com.example.gymtracker.ui.model.Exercise
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.workoutExerciseList.WorkoutExerciseListUiState
import com.example.gymtracker.ui.workoutExerciseList.WorkoutExerciseListViewModel
import com.example.gymtracker.utils.MainDispatcherRule
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

            assertEquals(exerciseList.filter { it.name.contains("") },
                (state as WorkoutExerciseListUiState.Success).state.exerciseList)

            // Check state filtering by name
            viewModel.updateSearchedExerciseName("exercise1")

            state = viewModel.workoutExerciseListScreenState.value

            assertEquals(
                exerciseList.filter { it.name.contains("exercise1") },
                (state as WorkoutExerciseListUiState.Success).state.exerciseList
            )
            assertEquals("exercise1", state.state.exerciseNameFilter)

            // Check State filtering by type
            viewModel.updateSearchedExerciseName("")
            viewModel.updateExerciseTypeFilter(ExerciseType.Arms)

            state = viewModel.workoutExerciseListScreenState.value

            assertEquals(exerciseList.filter { it.type == ExerciseType.Arms }, (state as WorkoutExerciseListUiState.Success).state.exerciseList)
            assertEquals(ExerciseType.Arms, state.state.exerciseTypeFiler)

            // Check state filtering by name and type
            viewModel.updateSearchedExerciseName("exercise1")
            viewModel.updateExerciseTypeFilter(ExerciseType.Arms)

            state = viewModel.workoutExerciseListScreenState.value
            assertEquals(exerciseList.filter { it.type == ExerciseType.Arms && it.name.contains("exercise1")}, (state as WorkoutExerciseListUiState.Success).state.exerciseList)
            assertEquals(ExerciseType.Arms, state.state.exerciseTypeFiler)
            assertEquals("exercise1", state.state.exerciseNameFilter)

            collectJob.cancel()

        }


    val exerciseList = listOf(
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
            id = 0,
            name = "exercise4",
            type = ExerciseType.Arms
        )
    )

}