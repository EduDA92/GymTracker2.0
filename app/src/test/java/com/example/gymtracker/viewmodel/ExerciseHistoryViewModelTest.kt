package com.example.gymtracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.gymtracker.testdoubles.repository.TestExerciseSetRepository
import com.example.gymtracker.ui.exerciseHistory.ExerciseHistoryUiState
import com.example.gymtracker.ui.exerciseHistory.ExerciseHistoryViewModel
import com.example.gymtracker.ui.exerciseHistory.HistoryState
import com.example.gymtracker.ui.exerciseHistory.SetHistory
import com.example.gymtracker.ui.exerciseHistory.SetHistoryItem
import com.example.gymtracker.ui.model.HistoryItem
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
import java.time.LocalDate
import kotlin.test.assertEquals

class ExerciseHistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var exerciseSetRepository: TestExerciseSetRepository
    private lateinit var viewModel: ExerciseHistoryViewModel

    @Before
    fun init(){

        exerciseSetRepository = TestExerciseSetRepository()
        viewModel = ExerciseHistoryViewModel(exerciseSetRepository,
            SavedStateHandle(mapOf("exerciseId" to 1L))
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun exerciseHistoryViewModel_exerciseHistoryUiStateValue_isCorrectlyModeled() = runTest {

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {viewModel.exerciseHistoryUiState.collect()}

        // Check initial state

        var state = viewModel.exerciseHistoryUiState.value

        assertEquals(ExerciseHistoryUiState.Loading, state)

        // emit data and check state format

        exerciseSetRepository.emitExerciseSetHistory(historyItemList)

        state = viewModel.exerciseHistoryUiState.value

        assertEquals(historyState, (state as ExerciseHistoryUiState.Success).state)

        collectJob.cancel()

    }


    private val historyItemList = listOf<HistoryItem>(
        HistoryItem(
            workoutName = "Monday Workout",
            exerciseName = "Squat",
            exerciseSetDate = LocalDate.now(),
            exerciseSetReps = 10,
            exerciseSetWeight = 100f
        ),
        HistoryItem(
            workoutName = "Monday Workout",
            exerciseName = "Squat",
            exerciseSetDate = LocalDate.now(),
            exerciseSetReps = 10,
            exerciseSetWeight = 100f
        ),
        HistoryItem(
            workoutName = "Monday Workout",
            exerciseName = "Squat",
            exerciseSetDate = LocalDate.now(),
            exerciseSetReps = 10,
            exerciseSetWeight = 100f
        ),
        HistoryItem(
            workoutName = "Tuesday Workout",
            exerciseName = "Squat",
            exerciseSetDate = LocalDate.now().plusDays(1),
            exerciseSetReps = 15,
            exerciseSetWeight = 150f
        ),
        HistoryItem(
            workoutName = "Tuesday Workout",
            exerciseName = "Squat",
            exerciseSetDate = LocalDate.now().plusDays(1),
            exerciseSetReps = 15,
            exerciseSetWeight = 150f
        ),
        HistoryItem(
            workoutName = "Tuesday Workout",
            exerciseName = "Squat",
            exerciseSetDate = LocalDate.now().plusDays(1),
            exerciseSetReps = 15,
            exerciseSetWeight = 150f
        )
    )

    private val historyState = HistoryState(
        exerciseName = "Squat",
        setHistoryList = persistentListOf(
            SetHistoryItem(
                workoutName = "Monday Workout",
                workoutDate = LocalDate.now(),
                setHistory = persistentListOf(
                    SetHistory(10,100f),
                    SetHistory(10,100f),
                    SetHistory(10,100f)
                )
            ),
            SetHistoryItem(
                workoutName = "Tuesday Workout",
                workoutDate = LocalDate.now().plusDays(1),
                setHistory = persistentListOf(
                    SetHistory(15,150f),
                    SetHistory(15,150f),
                    SetHistory(15,150f)
                )
            )
        )
    )

}